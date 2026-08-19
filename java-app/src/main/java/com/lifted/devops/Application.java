package com.lifted.devops;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Application {

    public static void main(String[] args) throws IOException {

        int port = 8081;

        HttpServer server = HttpServer.create(
            new InetSocketAddress(port),
            0
        );

        server.createContext("/", Application::handleRequest);

        server.setExecutor(null);

        System.out.println(
            "======================================"
        );

        System.out.println(
            "   Lifted DevOps Java Application"
        );

        System.out.println(
            "   Server running on port " + port
        );

        System.out.println(
            "======================================"
        );

        server.start();
    }

    private static void handleRequest(HttpExchange exchange)
            throws IOException {

        String response = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">

                    <title>Lifted Java Application</title>

                    <style>

                        * {
                            box-sizing: border-box;
                        }

                        body {
                            margin: 0;
                            min-height: 100vh;

                            display: flex;
                            align-items: center;
                            justify-content: center;

                            font-family: Arial, sans-serif;

                            background: #020617;
                            color: white;

                            text-align: center;
                        }

                        .container {
                            max-width: 700px;
                            padding: 50px;
                        }

                        h1 {
                            font-size: 3rem;
                            margin-bottom: 15px;
                        }

                        .highlight {
                            color: #60a5fa;
                        }

                        p {
                            color: #94a3b8;
                            font-size: 1.1rem;
                            line-height: 1.7;
                        }

                        .status {
                            display: inline-block;

                            margin-top: 25px;
                            padding: 12px 20px;

                            border-radius: 30px;

                            background: #14532d;
                            color: #86efac;

                            font-weight: bold;
                        }

                    </style>
                </head>

                <body>

                    <div class="container">

                        <h1>
                            Lifted
                            <span class="highlight">
                                Java Application
                            </span>
                        </h1>

                        <p>
                            This application is built with Java,
                            Maven and Docker.
                        </p>

                        <p>
                            It is running as a containerized
                            web application.
                        </p>

                        <div class="status">
                            Application Status: RUNNING
                        </div>

                    </div>

                </body>
                </html>
                """;

        exchange.getResponseHeaders()
                .set("Content-Type", "text/html; charset=UTF-8");

        exchange.sendResponseHeaders(
            200,
            response.getBytes().length
        );

        try (OutputStream output =
                     exchange.getResponseBody()) {

            output.write(response.getBytes());
        }
    }
}