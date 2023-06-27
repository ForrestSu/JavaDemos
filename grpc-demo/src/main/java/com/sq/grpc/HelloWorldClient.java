package com.sq.grpc;

import com.sq.grpc.proto.GreeterGrpc;
import com.sq.grpc.proto.HelloReply;
import com.sq.grpc.proto.HelloRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.concurrent.TimeUnit;


public class HelloWorldClient {

    private final ManagedChannel channel;
    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    /**
     * Construct client for accessing HelloWorld server using the existing channel.
     */
    HelloWorldClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    /**
     * Construct client connecting to HelloWorld server at {@code host:port}.
     */
    public HelloWorldClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext().build());
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /**
     * 向服务端发送 hello 消息.
     */
    public void greet(String name) {
        System.out.println("send => [" + name + "] ...");
        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
        HelloReply response;
        try {
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            System.out.println("RPC failed: " + e.getStatus());
            return;
        }
        System.out.println("recv <= " + response.getMessage());
    }

    public static void main(String[] args) throws Exception {
        // Access a service running on the local machine on port 50051
        HelloWorldClient client = new HelloWorldClient("localhost", 50051);
        try {
            String user = "world";
            client.greet(user);
        } finally {
            client.shutdown();
        }
    }
}