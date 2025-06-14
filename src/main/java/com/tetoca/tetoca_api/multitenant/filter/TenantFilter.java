package com.tetoca.tetoca_api.multitenant.filter;

import com.tetoca.tetoca_api.multitenant.context.TenantContextHolder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class TenantFilter implements Filter {

  private static final String TENANT_PREFIX = "/tenant/";
  private static final Set<String> IGNORED_PATHS = Set.of(
    "/auth", "/public", "/docs", "/favicon.ico"
  );

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String path = httpRequest.getRequestURI();

    if (!shouldIgnore(path)) {
      String tenantId = extractTenantIdFromPath(path);
      if (tenantId != null && !tenantId.isBlank()) {
        TenantContextHolder.setTenantId(tenantId);
      } else {
        throw new ServletException("Tenant ID is missing in path: " + path);
      }
    }

    try {
      chain.doFilter(request, response);
    } finally {
      TenantContextHolder.clear();
    }
  }

  private boolean shouldIgnore(String path) {
    return IGNORED_PATHS.stream().anyMatch(path::startsWith);
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
