package com.tetoca.tetoca_api.multitenant.filter;

import com.tetoca.tetoca_api.multitenant.context.TenantContextHolder;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Set;

@Component
public class TenantFilter implements Filter {

  private static final String DEFAULT_TENANT_ID = "default";
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
    String path = httpRequest.getRequestURI();

    String tenantId = DEFAULT_TENANT_ID;
    if (!shouldIgnore(path)) {
      String extractedTenantId = extractTenantIdFromPath(path);
      if (extractedTenantId != null) tenantId = extractedTenantId;
    }

    try {
      TenantContextHolder.setTenantId(tenantId);
      chain.doFilter(request, response);
    } finally {
      TenantContextHolder.clear();
    }
  }

  private boolean shouldIgnore(String path) {
    return IGNORED_PATHS.stream().anyMatch(path::startsWith);
  }

  /**
   * Extract the tenant ID of the URI
   * @return the tenant ID if foundd, or null if not.
   */
  private String extractTenantIdFromPath(String path) {
    if (path.startsWith(TENANT_PREFIX)) {
      String withoutPrefix = path.substring(TENANT_PREFIX.length());
      if (withoutPrefix.isBlank()) return null;

      int slashIndex = withoutPrefix.indexOf('/');
      String tenantId = (slashIndex != -1) ? withoutPrefix.substring(0, slashIndex) : withoutPrefix;
      return tenantId.isBlank() ? null : tenantId;
    }
    return null;
  }
}
