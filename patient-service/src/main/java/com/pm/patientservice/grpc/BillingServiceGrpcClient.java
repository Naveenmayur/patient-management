package com.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BillingServiceGrpcClient {

    private final BillingServiceGrpc.BillingServiceBlockingStub billingServiceStub;

    public BillingServiceGrpcClient(@Value("${billing.service.address:localhost}") String host,
                                  @Value("${billing.service.grpc.port:9001}") int port) {
        log.info("BillingServiceGrpcClient connecting to {}:{}", host, port);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        billingServiceStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    public BillingResponse createBillingAccount(String patientId, String name, String email) {
        BillingRequest request = BillingRequest.newBuilder()
                .setPatientId(patientId)
                .setName(name)
                .setEmail(email)
                .build();
        log.info("Sending billing account creation request for patientId: {}", patientId);
        BillingResponse response = billingServiceStub.createBillingAccount(request);
        log.info("Received billing account creation response for patientId: {}, success: {}", patientId, response);
        return response;
    }
}
