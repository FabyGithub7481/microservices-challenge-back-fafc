package com.client_person_service.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

  private String message;
  private String error;
  private int status;
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date date;
}
