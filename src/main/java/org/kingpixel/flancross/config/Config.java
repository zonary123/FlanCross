package org.kingpixel.flancross.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Data
public class Config {
  private String mongoURL;

  public void init() {
    File configFile = new File("config/flancross/config.json");

    // Asegurarse de que el directorio exista
    if (!configFile.getParentFile().exists()) {
      configFile.getParentFile().mkdirs();
    }

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    try {
      // Si el archivo NO existe, crearlo con valores por defecto
      if (!configFile.exists()) {
        this.mongoURL = "mongodb://localhost:27017"; // valor por defecto
        try (FileWriter writer = new FileWriter(configFile)) {
          gson.toJson(this, writer);
        }
      } else {
        // Si el archivo existe, leer los valores existentes
        try (FileReader reader = new FileReader(configFile)) {
          Config config = gson.fromJson(reader, Config.class);
          if (config != null) {
            this.mongoURL = config.getMongoURL();
          } else {
            this.mongoURL = "mongodb://localhost:27017"; // fallback
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
