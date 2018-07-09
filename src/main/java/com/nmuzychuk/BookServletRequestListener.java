package com.nmuzychuk;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebListener("/books")
public class BookServletRequestListener implements ServletRequestListener {
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ServletContext sc = sre.getServletContext();
        HttpServletRequest sr = (HttpServletRequest) sre.getServletRequest();
        Map<String, List<String>> params = Collections.list(sr.getParameterNames()).stream()
                .collect(Collectors.toMap((pn) -> pn,
                        (pn) -> Arrays.stream(sr.getParameterValues(pn)).collect(Collectors.toList())));
        String msg = sr.getMethod() + " " + sr.getRequestURL() + " " + params;
        sc.log(msg);
    }
}
