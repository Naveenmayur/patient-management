package com.pm.billingservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
public class BillingGrpcService extends BillingServiceImplBase {

    @Override
    public void createBillingAccount(BillingRequest billingRequest, StreamObserver<BillingResponse>  responseObserver) {

        log.info("createBillingAccount request={}",billingRequest);
        // Business logic
        BillingResponse billingResponse = BillingResponse.newBuilder()
                .setAccountId("123").setStatus("ACTIVE").build();

        responseObserver.onNext(billingResponse);
        responseObserver.onCompleted();
    }

}
