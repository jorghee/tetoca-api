package com.tetoca.tetoca_api.global.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CompanyRegistrationEvent extends ApplicationEvent {
  private final String tenantId;
  private final String dbName;
  private final String dbUri;
  private final String dbUser;
  private final String dbPassword;

  public CompanyRegistrationEvent(Object source, String tenantId, String dbName, 
      String dbUri, String dbUser, String dbPassword) {
    super(source);
    this.tenantId = tenantId;
    this.dbName = dbName;
    this.dbUri = dbUri;
    this.dbUser = dbUser;
    this.dbPassword = dbPassword;
  }
}
