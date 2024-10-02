package de.secretj12.turnierplaner.model;

public class jPage<T> {
  private long total;
  private T data;

  public jPage(long total, T data) {
    this.total = total;
    this.data = data;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }
}
