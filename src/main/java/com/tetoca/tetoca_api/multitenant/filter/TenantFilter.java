package com.tetoca.tetoca_api.multitenant.filter;

import com.tetoca.tetoca_api.multitenant.context.TenantContextHolder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class TenantFilter implements Filter {

  private static final String TENANT_PREFIX = "/tenant/";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String path = httpRequest.getRequestURI();

    String tenantId = extractTenantIdFromPath(path);
    if (tenantId != null && !tenantId.isBlank()) {
      TenantContextHolder.setTenantId(tenantId);
    }

    try {
      chain.doFilter(request, response);
    } finally {
      TenantContextHolder.clear();
    }
  }

  private String extractTenantIdFromPath(String path) {
    if (path.startsWith(TENANT_PREFIX)) {
      String withoutPrefix = path.substring(TENANT_PREFIX.length());
      int slashIndex = withoutPrefix.indexOf('/');
      return (slashIndex != -1) ? withoutPrefix.substring(0, slashIndex) : withoutPrefix;
    }
    return null;
  }
}
