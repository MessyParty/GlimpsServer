package com.glimps.glimpsserver.common.presentation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class GitVersionController {

  @GetMapping("/version")
  public String geVersion() {
    return getLastCommitInfo();
  }

  private static String getLastCommitInfo() {
    try {
      Process process = new ProcessBuilder("git", "log", "-1").start();
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

      StringBuilder result = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        result.append(line).append("\n");
      }

      int exitCode = process.waitFor();
      if (exitCode == 0) {
        return result.toString();
      } else {
        return "Failed to get Git commit information.";
      }
    } catch (IOException | InterruptedException e) {
      return "Error occurred while executing Git command: " + e.getMessage();
    }
  }
}
