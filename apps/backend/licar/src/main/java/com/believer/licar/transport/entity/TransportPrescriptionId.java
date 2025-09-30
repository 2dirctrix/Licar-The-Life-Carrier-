package com.believer.licar.transport.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class TransportPrescriptionId implements Serializable {

    private int transport;
    private int prescription;
}
