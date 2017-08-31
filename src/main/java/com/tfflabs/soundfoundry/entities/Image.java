package com.tfflabs.soundfoundry.entities;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Image {
	@Id
	private String url;
	private BigDecimal height;
	private BigDecimal width;

}
