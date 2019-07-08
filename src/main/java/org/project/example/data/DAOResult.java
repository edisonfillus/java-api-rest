package org.project.example.data;

import java.util.List;

public class DAOResult<T> {

    public String cursor;
    public List<T> result;
  
    public DAOResult(List<T> result, String cursor) {
      this.result = result;
      this.cursor = cursor;
    }
  
    public DAOResult(List<T> result) {
      this.result = result;
      this.cursor = null;
    }
    
}