import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.util.Properties;
import java.util.logging.*;
import java.io.*;
import java.net.InetSocketAddress;

public class MyHttpServer {

    //    to test $curl -s --data-binary @filename.json http://localhost:8080
    static Logger loggerJU = Logger.getLogger(MyHttpServer.class.getName());

    public static void main(String[] args) throws IOException{
        HttpServer server;
        try{
            LogManager.getLogManager().readConfiguration(MyHttpServer.class.
                    getResourceAsStream("logging.properties"));
        }catch (Exception e){
            System.out.println("Problem  with config file: exception caught : " + e.getMessage());
        }
        try {
            server= HttpServer.create(new InetSocketAddress(8080), 0);
            loggerJU.info("Server created successfully");
            server.createContext("/", new MyHandlerSender());
            loggerJU.info("Context created");
            server.setExecutor(null); // creates a default executor
            loggerJU.info("Executor is set");
            server.start();
            loggerJU.info("Server is started");

        }catch (IOException e){
            loggerJU.log(Level.INFO, "We get an exception: " + e.getMessage());
        }
    }

    static class MyHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream in = t.getRequestBody();
            byte [] buf = new byte[1024];
            String data = "";
            int read;
            while((read = in.read(buf)) != -1)
            {
                String l = new String(buf, 0, read);
                data += l;
            }
            in.close();
            System.out.println("kekos");
            Gson gson = new Gson();
            try {
                Object o = gson.fromJson(data, Object.class);
                System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(o));
            } catch (Exception e) {

                System.out.println("invalid json format");
            }
            t.close();
        }
    }

    static class MyHandlerSender implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream in = t.getRequestBody();
            byte [] buf = new byte[1024];
            String data = "";
            int read;
            while((read = in.read(buf)) != -1)
            {
                String l = new String(buf, 0, read);
                data += l;
            }
            in.close();
            String response;
            Gson gson = new Gson();
            try {
                Object o = gson.fromJson(data, Object.class);
                if(o == null){
                    response = "No such a file";
                    loggerJU.log(Level.INFO, "No such a file!");
                } else{
                    response = new GsonBuilder().setPrettyPrinting().create().toJson(o);
                    loggerJU.log(Level.INFO, "Response is: " + response);
                }
//                System.out.println(response);
            } catch (Exception e) {
//                response = "invalid json format";
                response = e.getMessage().split(": ")[1];
                loggerJU.log(Level.INFO, "Invalid Json format: ", response);
            }
            response += "\n";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
            t.close();
        }
    }
}




