# Finances
Finances ist eine Applikation welche es ermöglicht schnell und einfach Ausgaben sowie Einnahmen zu protokollieren und zu bearbeiten.

### Navigation
![alt text](https://github.com/BashkimHHU/Finances/blob/master/Markdown/Navigation.PNG)

Über die Navigation können wir alle Funktionen erreichen. Zu diesen gehören
1. Transaktion hinzufügen
2. Konto erstellen (in Arbeit)
3. Kontobilanzen anzeigen (in Arbeit)
4. Transaktionsverlauf anzeigen
5. Einstellungen

### [1] Transaktion hinzufügen
![alt text](https://github.com/BashkimHHU/Finances/blob/master/Markdown/MainContentFragment.PNG)

Dieses Eingabefeld ermöglicht das Protokollieren unseres Geldflusses. Dabei können die folgenden Informationen hinterlegt werden.
1. Betrag
2. Titel
3. Kategorie
4. Handelt es sich um eine Ausgabe oder Einnahme
5. Datum
6. Konto von welchem der Betrag gebucht wird
7. Zusätzliche Informationen

Zwingend notwendig ist ein Betrag. Fehlt dieser wird die Transaktion nicht gespeichert. 
Mit dem Button "OK" veranlassen wir das Speichern der Eingabe. Mithilfe des Button "ABORT" werden alle Eingabefelder geleert.

### [2 & 3]
![alt text](https://github.com/BashkimHHU/Finances/blob/master/Markdown/Create_Account.PNG)
![alt text](https://github.com/BashkimHHU/Finances/blob/master/Markdown/Account_Balance.PNG)

Ein Konto benötigt zum erfolgreichen erstellen **sowohl** einen Namen **als auch** den aktuellen Kontostand.
Bestätigt wird hier mit dem "ADD" Button.

Die Kontobilanz erstellt eine Auflistung aller hinterlegter Konten und berechnet in abhängigkeit aller getätigten Transaktionen
den aktuellen Kontostand. Dafür muss jedoch das in der Transaktion vorgesehen Feld für das Konto ausgefüllt werden.
Um ein Konto zu entfernen muss der entsprechende Eintrag ausgewählt werden und mit "DELETE" Button bestätigt werden.
Alle Transaktionen die an das gelöschte Konto gekoppelt sind bleiben unverändert.

### [4] Transaktionsverlauf
![alt text](https://github.com/BashkimHHU/Finances/blob/master/Markdown/Transactions.PNG)
![alt text](https://github.com/BashkimHHU/Finances/blob/master/Markdown/Transactions-Settings.PNG)

Hier können wir die in *[1]* erstellen Einträge auflisten. Diese sind standardmäßig auf sieben Einträgen limitiert,
falls dies nicht genügt können wir über das *Optionsmenu* weitere Transaktionen auflisten lassen.

Die Suchfunktion setzt seinen Filter auf alle aktuell angezeigten Transaktionen auf. Gefiltert wird jede Transaktionseigenschaft,
welches in *[1]* ausgefüllt wurde. Solange der Akku genügend geladen ist, reagiert die Suche dynamisch auf eine Änderung des
Suchkriteriums

Um ein Transaktion zu löschen oder zu bearbeiten muss die gewünschte Transaktion ausgewählt werden. Die ausgewählte Transaktion ist
durch eine leichte Graufärbung zu erkennen. Der Button "DELETE" löscht die markierte Transaktion. Der Button "INSPECT" öffnet eine neue 
Activity, welche das identische Layout aus *[1]* verwendet. Über diese Activity können wir jedes mögliche Feld nach belieben bearbeiten.

### [5] Einstellungen
![alt text](https://github.com/BashkimHHU/Finances/blob/master/Markdown/Settings.PNG)

Folgende Einstellungen stehen zur Auswahl
1. Benutzername
2. Incognito-Modus

Bei einer Änderung des Benutzernamens, wird dieser in der Navigation angezeigt.

Der Incognito-Modus markiert jede getätigte Transaktion als *vertraulich*. Transaktionen, welche in diesem Modus getätigt werden,
sind nur in diesem Modus im Transaktionsverlauf sichtbar. Alle anderen Transaktionen sind selbsverständlich weiterhin einsehbar.

![alt text](https://github.com/BashkimHHU/Finances/blob/master/Markdown/Notification.PNG)

### About

Alle in dieser Arbeit verwendeten Icons stammen aus: https://material.io/




