package com.nmuzychuk;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/books"}, asyncSupported = true)
public class BookServlet extends HttpServlet {
    static private Map<Integer, String> books;

    @Override
    public void init(ServletConfig servletConfig) {
        books = new HashMap<>();

        for (int i = 1; i <= 5; i++) {
            books.put(i, "Book" + i);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.startAsync();

        final String paramId = req.getParameter("id");
        final Writer out = resp.getWriter();

        final AsyncContext asyncContext = req.getAsyncContext();
        asyncContext.start(new Runnable() {
            @Override
            public void run() {
                try {
                    if (paramId == null) {
                        out.write(books.toString());
                    } else {
                        int id = Integer.parseInt(paramId);
                        out.write(books.get(id));
                    }
                    asyncContext.complete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println("adding book");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println("updating book");
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        out.println("deleting book");
    }
}
