package gr.cite.rda.web.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The RDA maDMP API specification names multi-value query parameters with a trailing "[]"
 * (e.g. languages[]=eng&languages[]=deu). Servlet parameter binding only sees the literal
 * name including the brackets, so this filter exposes every "name[]" parameter under the
 * bare "name" as well, merging values if both spellings are used.
 */
@Component
public class BracketParameterNameFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean hasBracketParams = false;
        for (Enumeration<String> names = request.getParameterNames(); names.hasMoreElements(); ) {
            if (names.nextElement().endsWith("[]")) {
                hasBracketParams = true;
                break;
            }
        }
        if (!hasBracketParams) {
            filterChain.doFilter(request, response);
            return;
        }

        Map<String, String[]> merged = new LinkedHashMap<>(request.getParameterMap());
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            if (!entry.getKey().endsWith("[]")) continue;
            String bareName = entry.getKey().substring(0, entry.getKey().length() - 2);
            if (bareName.isBlank()) continue;
            String[] existing = merged.get(bareName);
            if (existing == null) {
                merged.put(bareName, entry.getValue());
            } else {
                String[] combined = Arrays.copyOf(existing, existing.length + entry.getValue().length);
                System.arraycopy(entry.getValue(), 0, combined, existing.length, entry.getValue().length);
                merged.put(bareName, combined);
            }
        }
        Map<String, String[]> parameterMap = Collections.unmodifiableMap(merged);

        filterChain.doFilter(new HttpServletRequestWrapper(request) {
            @Override
            public String getParameter(String name) {
                String[] values = parameterMap.get(name);
                return values == null || values.length == 0 ? null : values[0];
            }

            @Override
            public Map<String, String[]> getParameterMap() {
                return parameterMap;
            }

            @Override
            public Enumeration<String> getParameterNames() {
                return Collections.enumeration(parameterMap.keySet());
            }

            @Override
            public String[] getParameterValues(String name) {
                return parameterMap.get(name);
            }
        }, response);
    }
}
