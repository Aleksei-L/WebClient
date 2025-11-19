package org.web.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.jetbrains.annotations.NotNull;
import org.web.client.generated.LoginRequest;
import org.web.client.generated.LoginResponse;
import org.web.client.generated.LoginServiceGrpc;

import java.util.Scanner;

public final class Main {
    @NotNull
    private static final ManagedChannel channel = ManagedChannelBuilder
            .forAddress("localhost", 5000)
            .usePlaintext() // gRPC по умолчанию требует TLS, отключаем
            .build();

    @NotNull
    private static final Scanner scanner = new Scanner(System.in);

    @NotNull
    private static final StreamObserver<LoginResponse> streamObserver = new StreamObserver<>() {
        @Override
        public void onNext(LoginResponse value) {
            System.out.println("Response (token): " + value.getToken());
        }

        @Override
        public void onError(Throwable t) {
            System.out.println("Error: " + t.getMessage());
        }

        @Override
        public void onCompleted() {
        }
    };

    static void main() {
        try {
            while (true) {
                var stub = LoginServiceGrpc.newStub(channel);

                System.out.println("Enter username (or \"exit\"): ");
                var username = scanner.next();
                if (username.equals("exit"))
                    break;

                System.out.println("Enter password: ");
                var password = scanner.next();

                var request = LoginRequest.newBuilder().setUsername(username).setPassword(password).build();

                stub.login(request, streamObserver);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            channel.shutdown();
        }
    }
}
