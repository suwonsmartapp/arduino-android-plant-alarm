#include <Debug.h>
#include <WiFly.h>
#include <WiFlyClient.h>

#include <SoftwareSerial.h>
#include "WiFly.h"

#define SSID      "test"
#define KEY       "123456789a"
// check your access point's security mode, mine was WPA20-PSK
// if yours is different you'll need to change the AUTH constant, see the file WiFly.h for avalable security codes
#define AUTH      WIFLY_AUTH_WPA2_PSK

int flag = 0;
// Pins' connection
// Arduino       WiFly
//  2    <---->    TX
//  3    <---->    RX

SoftwareSerial wiflyUart(2, 3); // create a WiFi shield serial object
WiFly wifly(&wiflyUart); // pass the wifi siheld serial object to the WiFly class

int MoisturePin = A0;    // A0에 입력 MoisturePin을 연결
int sensorValue = 0;  // 센서로부터 나오는 값을 변수에 저장

void setup()
{
  wiflyUart.begin(9600); // start wifi shield uart port

  Serial.begin(9600); // start the arduino serial port
  Serial.println("--------- WIFLY Webserver --------");

  // wait for initilization of wifly
  delay(1000);

  wifly.reset(); // reset the shield
  delay(1000);
  //set WiFly params

  wifly.sendCommand("set ip local 80\r"); // set the local comm port to 80
  delay(100);

  wifly.sendCommand("set comm remote 0\r"); // do not send a default string when a connection opens
  delay(100);

  wifly.sendCommand("set comm open *OPEN*\r"); // set the string that the wifi shield will output when a connection is opened
  delay(100);

  Serial.println("Join " SSID );
  if (wifly.join(SSID, KEY, AUTH)) {
    Serial.println("OK");
  } else {
    Serial.println("Failed");
  }

  wifly.sendCommand("get ip\r");
  char c;

  while (wifly.receive((uint8_t *)&c, 1, 300) > 0) { // print the response from the get ip command
    Serial.print((char)c);
  }

  Serial.println("Web server ready");
}

void loop()
{

  if (wifly.available())
  { // the wifi shield has data available
    boolean currentLineIsBlank = true;
    delay(100);
    if (wiflyUart.find("*OPEN*")) // see if the data available is from an open connection by looking for the *OPEN* string
    {
      Serial.println("New Browser Request!");
      delay(1000); // delay enough time for the browser to complete sending its HTTP request string

      while (currentLineIsBlank) {
        Serial.print("Moisture Sensor = " );
        Serial.println(sensorValue);

        // send HTTP header
        wiflyUart.println("HTTP/1.1 200 OK");
        wiflyUart.println("Content-Type: application/json; charset=UTF-8");
        wiflyUart.println("Content-Length: 500"); // length of HTML code
        wiflyUart.println("Connection: close");
        wiflyUart.println();

        // send webpage's HTML code
        wiflyUart.print("{ \"moisture\": ");
        wiflyUart.print(analogRead(MoisturePin));
        wiflyUart.print(" }");
        break;
      }
    }
  }
}
