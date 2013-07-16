/**
 *
 * @author Wei-Ming Wu
 *
 *
 * Copyright 2013 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package wmw.util.smartcard;

import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import com.google.common.base.Objects;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;

@SuppressWarnings("restriction")
public final class CardReader {

  public static byte[] SelectAPDU = new byte[] { (byte) 0x00, (byte) 0xA4,
      (byte) 0x04, (byte) 0x00, (byte) 0x10, (byte) 0xD1, (byte) 0x58,
      (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
      (byte) 0x00, (byte) 0x00, (byte) 0x11, (byte) 0x00 };

  private CardReader() {}

  public static Set<CardResponse> read(CommandAPDU command) {
    Set<CardResponse> responses = newHashSet();
    for (CardTerminal terminal : getCardTerminals()) {
      try {
        Card card = terminal.connect("*");
        CardChannel channel = card.getBasicChannel();
        channel.transmit(new CommandAPDU(SelectAPDU));
        ResponseAPDU response = channel.transmit(command);
        responses.add(new CardResponse(channel.getChannelNumber(), response
            .getData()));
      } catch (CardException e) {
        Logger.getLogger(CardReader.class.getName()).log(Level.SEVERE, null,
            e.getMessage());
      }
    }
    return responses;
  }

  private static List<CardTerminal> getCardTerminals() {
    TerminalFactory factory = TerminalFactory.getDefault();
    List<CardTerminal> terminals = emptyList();
    try {
      terminals = factory.terminals().list();
    } catch (CardException e) {
      Logger.getLogger(CardReader.class.getName()).log(Level.SEVERE, null,
          e.getMessage());
    }
    return terminals;
  }

  public String toString() {
    return Objects.toStringHelper(CardReader.class).toString();
  }

}