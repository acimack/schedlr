package edu.brown.cs.schedlr.database;

public class Singleton {
  private static final Singleton INSTANCE = new Singleton();
  private Query query;

  /**
   * Method to return the one instance of the singleton.
   * @return a Singleton
   */
  public static Singleton getInstance() {
    return INSTANCE;
  }

  /**
   * Method to access the query class, which houses all of our queries.
   * @return a query
   */
  public Query getQuery() {
    return this.query;
  }

  /**
   * Method to set the query.
   * @param query a query
   */
  public void setQuery(Query query) {
    this.query = query;
  }
}
