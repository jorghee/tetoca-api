package com.tetoca.tetoca_api.multitenant.filter;

import com.tetoca.tetoca_api.multitenant.context.TenantContextHolder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Set;

@Component
public class TenantFilter implements Filter {

  private final String TENANT_PREFIX;
  private final Set<String> IGNORED_PATHS;

  public TenantFilter(
      @Value("${multitenant.tenant-prefix}") String tenantPrefix,
      @Value("${multitenant.ignored-paths}") Set<String> ignoredPaths) {
    this.TENANT_PREFIX = tenantPrefix;
    this.IGNORED_PATHS = ignoredPaths;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    String path = httpRequest.getRequestURI();

    if (!shouldIgnore(path)) {
      String tenantId = extractTenantIdFromPath(path);
      if (tenantId == null || tenantId.isBlank()) {
        httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, 
            "Missing or invalid tenant identifier");
        return;
      }
      TenantContextHolder.setTenantId(tenantId);
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
    return "default";
  }
}
