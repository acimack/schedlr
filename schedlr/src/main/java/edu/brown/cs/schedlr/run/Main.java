package edu.brown.cs.schedlr.run;

import com.google.gson.Gson;

import edu.brown.cs.schedlr.database.Query;
import edu.brown.cs.schedlr.database.Singleton;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import spark.Spark;

/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;
  private static final int HUND = 100;
  private static final Gson GSON = new Gson();
  private static final Singleton DATABASE = Singleton.getInstance();


  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private String[] args;

  private Main(String[] args) {
    this.args = args;
  }

  private void run() {
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("traffic");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
      .defaultsTo(DEFAULT_PORT);
    parser.accepts("clear");
    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

    try {
      // TODO don't hardcode the path
      DATABASE.setQuery(new Query("data/schedlrdb.sqlite3"));
      if (options.has("clear")) {
        DATABASE.getQuery().clearAll();
      }
    } catch (Exception e) {
      // TODO informative message
      e.printStackTrace();
    }

  }

  /**
   * Running the server.
   * @param port int, port server is run on
   */
  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");

    Spark.options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");

      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });

    Spark.before((request, response) -> {
      response.header("Access-Control-Allow-Origin", "http://localhost:3000");
      response.header("Access-Control-Allow-Headers", "*");
      response.type("application/json");
      response.header("Access-Control-Allow-Credentials", "true"); });
    Spark.exception(Exception.class, new FrontendRequestHandler.ExceptionPrinter());
    Spark.post("/addtask", new FrontendRequestHandler.AddTaskHandler());
    Spark.post("/schedule", new FrontendRequestHandler.ScheduleHandler());
    Spark.post("/alreadyscheduled", new FrontendRequestHandler.AlreadyScheduledHandler());
    Spark.post("/togglecompletion", new FrontendRequestHandler.ToggleAssignedCompletionHandler());
    Spark.post("/setdailytimes", new FrontendRequestHandler.SetDailyTimesHandler());
    Spark.post("/toggletaskcompletion", new FrontendRequestHandler.ToggleTaskCompletionHandler());
    Spark.post("/deletetask", new FrontendRequestHandler.DeleteTaskHandler());
    Spark.post("/modifytask", new FrontendRequestHandler.ModifyTaskHandler());
    Spark.post("/gettasks", new FrontendRequestHandler.GetTasksHandler());
    Spark.post("/getdailytimes", new FrontendRequestHandler.GetDailyTimesHandler());
    Spark.post("/login", new FrontendRequestHandler.LoginHandler());

  }
}
