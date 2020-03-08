package com.nmuzychuk;

import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@WebServlet(urlPatterns = {"/books"}, asyncSupported = true)
public class BookServlet extends HttpServlet {
    static private RedissonClient redisson;
    static private RList<Book> books;
    static private AtomicInteger id = new AtomicInteger();

    @Override
    public void init(ServletConfig servletConfig) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        config.setCodec(new org.redisson.codec.JsonJacksonCodec());
        redisson = Redisson.create(config);
        books = redisson.getList("bookList");

        // Add 5 books
        for (id.incrementAndGet(); id.get() <= 5; id.incrementAndGet()) {
            if (books.get(id.get()) == null) {
                Book book = new Book(id.get(), "Book");
                books.add(book);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.startAsync();

        final String paramId = req.getParameter("id");
        final Writer out = resp.getWriter();

        final AsyncContext asyncContext = req.getAsyncContext();
        asyncContext.start(() -> {
            try {
                if (paramId == null) {
                    out.write(books.toString());
                } else {
                    Optional<Book> optionalBook = books.stream().filter((b) -> b.getId() == Integer.parseInt(paramId)).findFirst();
                    if (optionalBook.isPresent()) {
                        out.write(optionalBook.get().toString());
                    } else {
                        resp.sendError(404);
                    }
                }
                asyncContext.complete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void doPost(HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        req.startAsync();

        final String paramName = req.getParameter("name");
        final Writer out = resp.getWriter();

        final AsyncContext asyncContext = req.getAsyncContext();
        asyncContext.start(() -> {
            try {
                Book book = new Book(id.getAndIncrement(), paramName);
                books.add(book);
                resp.setStatus(201);
                out.write(book.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            asyncContext.complete();
        });
    }

    @Override
    protected void doPut(HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        req.startAsync();

        final String paramId = req.getParameter("id");
        final String paramName = req.getParameter("name");
        final Writer out = resp.getWriter();

        final AsyncContext asyncContext = req.getAsyncContext();
        asyncContext.start(() -> {
            try {
                if (paramId == null) {
                    resp.sendError(400);
                } else {
                    Optional<Book> optionalBook = books.stream().filter((b) -> b.getId() == Integer.parseInt(paramId)).findFirst();
                    if (optionalBook.isPresent()) {
                        optionalBook.get().setName(paramName);
                        out.write(optionalBook.get().toString());
                    } else {
                        resp.sendError(404);
                    }
                }
                asyncContext.complete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void doDelete(HttpServletRequest req, final HttpServletResponse resp) {
        req.startAsync();

        final String paramId = req.getParameter("id");

        final AsyncContext asyncContext = req.getAsyncContext();
        asyncContext.start(() -> {
            try {
                if (paramId == null) {
                    resp.sendError(400);
                } else {
                    Optional<Book> optionalBook = books.stream().filter((b) -> b.getId() == Integer.parseInt(paramId)).findFirst();
                    if (optionalBook.isPresent()) {
                        books.remove(optionalBook.get());
                        resp.setStatus(204);
                    } else {
                        resp.sendError(404);
                    }
                }
                asyncContext.complete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void destroy() {
        redisson.shutdown();
    }
}
