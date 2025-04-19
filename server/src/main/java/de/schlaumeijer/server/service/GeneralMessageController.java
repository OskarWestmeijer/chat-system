package de.schlaumeijer.server.service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneralMessageController {

  private static GeneralMessageController instance;

  public static synchronized GeneralMessageController getInstance() {
    if (GeneralMessageController.instance == null) {
      GeneralMessageController.instance = new GeneralMessageController();
    }
    return GeneralMessageController.instance;
  }

}
