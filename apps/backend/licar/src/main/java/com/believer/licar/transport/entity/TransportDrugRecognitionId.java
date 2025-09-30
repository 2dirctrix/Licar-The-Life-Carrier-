package com.believer.licar.transport.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class TransportDrugRecognitionId implements Serializable {

    private int transport;
    private int drug;
}
