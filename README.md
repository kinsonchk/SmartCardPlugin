# About
A Minecraft Java Spigot plugin that adds Automated Fare Collection (AFC) systems to servers with public transportation systems. It includes a smart card system, ticket vending machines, transit passes, fare gates, payment machines, and more!

The development of this plugin was inspired by i998979's "Essencard" plugin. We added many more features that are not found in that plugin.

# Features
Using the plugin, server admins (and transit operators) can configure a customized AFC system tailored to their specific needs and collect fares. The plugin also supports servers with more than one transportation system; separated AFC systems can be defined, each with a different transit card name, currency, fare scheme, fare gate policy, etc.

The AFC system in this plugin consists of 4 main components:
## 1. Fare Media
### 1.1 Smart Cards (Stored-Value Cards)
Smart cards are represented as customized name tags in Minecraft. Examples of stored-value smart cards in real life: Hong Kong's Octopus card, London's Oyster card, Sydney's Opal card, New York City's OMNY card.

To obtain a smart card: Players must get one from a self-service machine for a non-refundable fee (can be customized per transit operator). A player can obtain an unlimited amount of smart cards. An admin can manually obtain a smart card linked to a particular transit operator and specify its expiry time for no cost by using "/smartcard getcard agencyID expiryTime".

Initially, a card contains no money. Players must use a self-service machine to add balance, and the corresponding cost will be deducted from the player's balance. If the transit operator uses a different currency, an exchange rate will be applied (can be customized). Money stored inside a smart card cannot be withdrawn. There is a maximum amount of money that can be loaded onto the card (can be customized per transit operator). An admin can manually set a smart card's balance by using "/smartcard cardbalance cardID moneyAmount".

Each card has a unique cardID, and is linked to the transit operator that issued it and cannot be changed. A card stores its remaining balance and the last 5 transaction records. A card can expire after a certain time period (can be customized per transit operator) from the last transaction. A card is not linked to a player and can be used by any player.

To use a smart card: right-click on designated signs while holding the card to activate fare gates/validators and use payment terminals. It is possible to make smart cards useable across different transit agencies.

A card can be manually deleted from the system by an admin using "/smartcard deletecard cardID".

### 1.2 Tickets
Tickets are represented as customized paper items in Minecraft. Tickets do not have IDs. A ticket is only usable within the transportation system of the transit operator that issued it.
#### 1.2.1 Single-Journey Tickets
As the name suggests, these can only be used for a single trip.

To obtain a ticket: players must buy one from a self-service machine. An admin (or staff) can manually obtain a ticket and specify its expiry time using "/smartcard ticket agencyID origin destination expiryTime" for no cost.

Each ticket is linked to the transit operator that issued it. The ticket stores the origin and destination station. These cannot be modified once the ticket is issued. A ticket can expire after a certain time period (can be customized per transit operator). A ticket is not linked to a player and can be used by any player.

To use a ticket: right-click on designated signs while holding the ticket to activate fare gates/validators.

#### 1.2.2 Exit-only Tickets
Should a player lose their smart card/ticket/transit pass during their journey, as long as they have a personal balance greater than $0, they can purchase an exit-only ticket for a surcharge (can be set per transit operator; usually the highest possible fare within the transportation network) using a self-service machine. They can use it at an exit fare gate to leave the transportation system. An exit-only ticket can only be used at an exit fare gate/validator once.

An admin (or staff) can manually obtain an exit-only ticket using "/smartcard exitonly agencyID" for no cost.

### 1.3 Passes (Storing Trip-based/Pre-defined Fares)
Transit passes are represented as customized books in Minecraft. Relatable examples in real life: Montreal's OPUS card system.
#### 1.3.1 Transit Passes
Each transit operator can define multiple pass types (each type to be identified by a passType) for different purposes. For example: a day/week/month pass, or a 1/2/10-trip pass, or a single/multi/all-zone pass, or a combination of them.

To obtain a pass: players must buy one from a self-service machine. An admin (or staff) can manually obtain a pass using "/smartcard getpass agencyID passType" for no cost.

Each pass has a unique passID. A pass is linked to the transit operator that issued it and cannot be changed. The pass type stored on the pass cannot be changed. A pass can expire after a certain time period, and/or be useless after a specified amount of uses, depending on the pass type. A pass is not linked to a player and can be used by any player. A pass cannot be reloaded after all its contents have been used up.

To use a pass: right-click on designated signs while holding the pass to activate fare gates/validators. It is possible to make a pass useable across different transit agencies.

A pass can be manually deleted from the system by an admin using "/smartcard deletepass passID".
#### 1.3.2 Staff Passes
Staff passes allow pass holders (usually staff members) to travel for free and access restricted areas. A staff pass can activate any fare validation device with no cost. A staff pass is only usable within the transportation system of the transit operator that issued it.

A staff pass does not have an ID. A staff pass cannot be obtained by an ordinary player. An admin (or staff) can manually obtain a staff pass using "/smartcard staffpass agencyID".


## 2. Fare Distribution
### 2.1 Self-service Machine
Each transit operator can deploy self-service machines across its transportation network, usually at metro station concourses. A machine is represented as a customized sign in Minecraft. A player can access a machine by right-clicking on the sign, which will display a menu. A self-service machine sign must use a specific format:

| Line             | Content                   | Example   |
| ---------------- | ------------------------- | --------- |
| 1 (as-is)        | [Tickets]                 | [Tickets] |
| 2 (arguments)    | agencyID (currentStation) | MTR TSY   |
| 3 (customizable) |                           |           |
| 4 (customizable) |                           |           |

A machine can be configured to be at a specified station, so that fare calculation can be based on that origin station. This can be achieved by indicating the current station code/zone on line 2 of a sign.

An admin can manually open a transit operator's self-service machine menu with "/smartcard machine agencyID currentStation".

A self-service machine has the following functions (some can be disabled by customizing config.yml):
#### 2.1.1 Function 1: Buy Ticket
Ticket cost is based on the origin and destination. This option will display a submenu allowing a player to select the origin and destination station within the transportation network. (If the self-service machine sign contains the current station code/zone, it indicates the origin station, and a player would then only need to select the destination.) Then, the correct fare will be calculated and the player will be able to buy the ticket. Tickets are non-refundable.
#### 2.1.2 Function 2: Buy a Pass
This option will display a submenu showing all the pass types defined by the transit operator allowing for purchase. A pass is non-refundable.
#### 2.1.3 Function 3: Buy a Smart Card
An initial non-refundable card purchase cost (can be customized) will be deducted from the player's balance and a card will be issued to the player. If the player has insufficient balance, the transaction will cancel and no card will be issued. A card is non-refundable.
#### 2.1.4 Function 4: Smart Card Add-value
A player needs to be holding a smart card to use this option. Then, a submenu will be displayed to allow the player to add money in specific increments (can be customized) to the card. Money added to the card cannot be withdrawn.
#### 2.1.5 Function 5: Fare Enquiry
This option allows a player to check details about their smart card, pass or ticket. A player needs to be holding a smart card/pass/ticket to use this option. The self-service machine menu will then disappear and the details will be displayed in chat. If it’s a smart card, its transaction records will also be displayed.

An admin can use "/smartcard enquiry card/pass cardID/passID" to manually check the details of a smart card or a pass.
#### 2.1.6 Function 6: Stuck in Paid-Area
This option allows a player to obtain an exit-only ticket for a surcharge to use an exit fate gate/validator to leave the transportation system, should the player lose their smart card/ticket/pass during their journey.


## 3. Fare Validation
Fare validation devices are represented as customized signs. The signs must use a specific format:

| Line on the sign                                                                     | Content                     | Example 1    | Example 2        | Example 3      | Example 4    |
| ------------------------------------------------------------------------------------ | --------------------------- | ------------ | ---------------- | -------------- | ------------ |
| 1 (as-is; see deviceFunction list below; see 3.2 Fare Validation Device Types below) | [deviceFunction deviceType] | [Entry Gate] | [Exit Validator] | [Pay Terminal] | [Staff Gate] |
| 2 (arguments)                                                                        | agencyID (line2Arguments)   | MTR ADM      | LRT 3 12.5       | MTR 45.6       | MTR          |
| 3 (customizable)                                                                     |                             |              |                  |                |              |
| 4 (customizable)                                                                     |                             |              |                  |                |              |

Each device can be serving one of these functions; each function requires certain arguments to be present on the sign to work:

| deviceFunction | line2Arguments (optional argument) | Example | Description                                                                                                   |
| -------------- | ---------------------------------- | ------- | ------------------------------------------------------------------------------------------------------------- |
| Entry          | stationCode/Zone (additionalMoney) | ADM     | Entry point to a transportation network; additionalMoney to collect when activated, can leave blank           |
| Exit           | stationCode/Zone (additionalMoney) | 3 12.5  | Exit point of a transportation network; additionalMoney to collect when activated, can leave blank            |
| Pay            | moneyAmount (stationCode/Zone)     | 45.6    | Deduct money directly from the card when used; stationCode/Zone is used for certain purposes, can leave blank |
| Staff          |                                    | Staff   | Can only be used with staff passes; there are no arguments                                                    |

If there are issues (not caused by a player attempting to fare evade) with a smart card, an admin can manually reset (clear) the current entry and exit record on the card with "/smartcard fixcard cardID".

### 3.1 Fare Validation Device Functions
#### 3.1.1 Function 1: Using an Entry device
- For smart cards: As long as the card's balance is greater than $0, the device will be activated. An entry record with the station code/zone and timestamp will be loaded to the card.
	- If there has already been an entry record, a surcharge fee (can be set per transit operator; usually the highest possible fare within the transportation network) will be deducted from the card's balance. And if the card's balance is still greater than $0, the device will still be activated; if not, the device won't be activated (must add value to greater than $0 to use the card again).
- For single-journey tickets: A ticket will activate an entry device only if its origin station/zone matches that of the entry device. Upon successful activation of an entry device, the enteredGate value on the ticket will be updated to true.
	- If the enteredGate value is already true, the device won't be activated.
- An exit-only ticket cannot be used with an entry device (obviously).
- For passes: As long as all the contents of the pass have not yet been used up, and all the conditions of the pass are valid, the device will be activated; if not, the device won't be activated. Upon successful activation of an entry device, the enteredGate value on the pass will be updated to true. Depending on the pass type, trip deduction will occur upon successful activation of an entry device.

#### 3.1.2 Function 2: Using an Exit device
- For smart cards: No matter whether the card's balance after deducting from a journey fare would result in a negative balance, an exit device can still be activated. An exit record with the station code/zone and timestamp will be loaded to the card, and the correct fare will be calculated and deducted from the card. The fare will then be added to the transaction record list.
	- If no prior entry record was found, a surcharge fee (can be set) will be deducted from the card's balance in addition to the normal correct fare. The device will still be activated.
- For tickets: A ticket will activate an exit device only if its destination station/zone matches that of the exit device. Upon successful activation of an exit device, the ticket will be confiscated.
	- If the enteredGate value is not already true, the device won't be activated.
- An exit-only ticket can activate any exit device of the same transit company that issued it. Upon successful activation, the ticket will be confiscated.
- For passes: As long as all the contents of the pass have not yet been used up, and all the conditions of the pass are valid, the device will be activated; if not, the device won't be activated. Trip deduction (if applicable) will occur upon successful activation of an exit device, unless the enteredGate value on the pass was true. Upon successful activation of an exit device, the enteredGate value on the pass will be updated to false.

#### 3.1.3 Function 3: Using a Pay device
Pay devices can be used for transit lines where there is one station that has no fare gates/validators (example: Hong Kong Airport Express).
- For smart cards: As long as the card's balance is greater than $0, the device will be activated. The fare will then be directly added to the transaction record list (no entry and exit record required).
- For tickets: A ticket can only activate pay devices with the same entry or exit station code/zone specified. Upon successful activation of a pay device, the ticket will directly be confiscated by the device.
- An exit-only ticket cannot be used with a pay device.
- For passes: A pass can only activate pay devices with the station code/zone specified. As long as all the contents of the pass have not yet been used up, and all the conditions of the pass are valid, the device will be activated; if not, the device won't be activated. Depending on the pass type, trip deduction will occur upon successful activation of a pay device.

#### 3.1.4 Function 4: Using a Staff device
Only staff passes can activate a staff device. A staff pass can activate any staff device of the same transit operator that issued the pass.

### 3.2 Fare Validation Device Types
There are three types of fare validation devices. They only differ in their redstone mechanism. Some of them will activate a redstone block which allows setting up contraptions.
#### 3.2.1 Device Type 1: Fare Gates
deviceType: "Gate"

Redstone mechanics:
- Upon successful activation of a gate, the block right below the sign will be changed to a green wool block for a certain period of time (can be set per transit operator), and after that, to a redstone block.
- For wide fare gates (2 blocks wide) using iron trapdoors: upon the opening of a gate, a connected iron trapdoor will also automatically open when placed in the correct orientation.
#### 3.2.2 Device Type 2: Fare Validators
deviceType: "Validator"

These are for transit systems with no fare gates (honour system). Example: Hong Kong Light Rail.

Redstone mechanics: none
#### 3.2.3 Device Type 3: Payment Terminals
deviceType: "Terminal"

These are the machines that allow one-time payments, such as item purchase at a convenience store.

Redstone mechanics: A dropper should be placed at the block right behind where the wall sign is located. Upon successful transaction, the dropper will be activated, giving one item.


## 4. Fare Processing
A transit operator can earn money from fare payments. Admins can use "/smartcard revenue agencyID" to see revenue information of the specified transit operator (how much it earned), and "/smartcard withdraw agencyID moneyAmount adminName" to transfer money from the transit operator's revenue to an admin's (or a staff member’s) personal balance. The revenue cannot be manually modified, and once money is withdrawn, it cannot be deposited back.

### 4.1 Fare Calculation
The plugin supports both distance-based and zone-based fare calculations. They basically function the same in this plugin: each fare combination between two stations/zones can be defined through a fare table (sometimes also referred to as a fare chart) in CSV format. Each transit operator can have a separate fare table CSV file tailored to them. The CSV files must be placed in a designated location in the server’s directory (in the "fare_charts" folder).

Example: MTR with smart cards (Octopus card) (using a small number of stations for demonstration)
- Column is for origin stations; row is for destination stations
- Note: It is possible to set the fare for entering and exiting at the same station (see from KET to KET, for example)

Source: https://www.mtr.com.hk/en/customer/tickets/octopus_fares.html

|     | KET | HKU | SYP | SHW | CEN | ADM | WAC | CAB |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| KET | 5.0 | 4.9 | 4.9 | 5.9 | 5.9 | 5.9 | 7.4 | 7.4 |
| HKU | 4.9 | 5.0 | 4.9 | 4.9 | 5.9 | 5.9 | 5.9 | 7.4 |
| SYP | 4.9 | 4.9 | 5.0 | 4.9 | 4.9 | 5.9 | 5.9 | 5.9 |
| SHW | 5.9 | 4.9 | 4.9 | 5.0 | 4.9 | 4.9 | 5.9 | 5.9 |
| CEN | 5.9 | 5.9 | 4.9 | 4.9 | 5.0 | 4.9 | 4.9 | 5.9 |
| ADM | 5.9 | 5.9 | 5.9 | 4.9 | 4.9 | 5.0 | 4.9 | 4.9 |
| WAC | 7.4 | 5.9 | 5.9 | 5.9 | 4.9 | 4.9 | 5.0 | 4.9 |
| CAB | 7.4 | 7.4 | 5.9 | 5.9 | 5.9 | 4.9 | 4.9 | 5.0 |

Another example: zone-based fares (such as Hong Kong LRT)

|       | Zone1 | Zone2 | Zone3 | Zone4 | Zone5 |
| ----- | ----- | ----- | ----- | ----- | ----- |
| Zone1 | 5.5   | 5.5   | 6.5   | 8.0   | 8.0   |
| Zone2 | 5.5   | 5.5   | 5.5   | 6.5   | 8.0   |
| Zone3 | 6.5   | 5.5   | 5.5   | 5.5   | 6.5   |
| Zone4 | 8.0   | 6.5   | 5.5   | 5.5   | 5.5   |
| Zone5 | 8.0   | 8.0   | 6.5   | 5.5   | 5.5   |

If a combination is missing or no such combination is defined when calculating the fare, the fare will be treated as $0 by default.

It is possible to set a temporary network-wide fare discount, or allow free travel across the network, such as when there are special events on a server. This can be done by changing the fare multiplier value in config.yml, so that when calculating the fare, the normal fare defined in the fare chart will be multiplied by that value, which will be the final fare deducted from a player.

### 4.2 Special Cases (to be implemented later or not implemented)
Potential ones (MTR):
- TST-ETST transfer within 3 mins
- MEF 2-min paid area fare exemption

# Commands and Permissions
## Commands

| Command (optional arugments in brackets)                                                           | Permission | Description                                                                                    |
| -------------------------------------------------------------------------------------------------- | ---------- | ---------------------------------------------------------------------------------------------- |
| /smartcard getcard [agencyID] ([expiryTime])                                                       | admin      | Obtain a smart card. If expiryTime is not specified, use the default one in config.            |
| /smartcard cardbalance [cardID] [moneyAmount]                                                      | admin      | Set the money balance of the specified card.                                                   |
| /smartcard fixcard [cardID]                                                                        | admin      | Clears the current entryRecord and exitRecord of a smart card.                                 |
| /smartcard deletecard [cardID]                                                                     | admin      | Deletes a smart card from the database.                                                        |
| /smartcard ticket [agencyID] [originStationCode/Zone] [destinationStationCode/Zone] ([expiryTime]) | admin      | Obtain a single-journey ticket. If expiryTime is not specified, use the default one in config. |
| /smartcard exitonly [agencyID]                                                                     | admin      | Obtain an exit-only ticket.                                                                    |
| /smartcard getpass [agencyID] [passType]                                                           | admin      | Obtain a pass.                                                                                 |
| /smartcard deletepass [passID]                                                                     | admin      | Deletes a pass from the database.                                                              |
| /smartcard staffpass [agencyID]                                                                    | admin      | Obtain a staff pass.                                                                           |
| /smartcard machine [agencyID] ([currentStation])                                                   | admin      | Manually open the self-service machine menu of the specified transit operator.                 |
| /smartcard enquiry card/pass [cardID/passID]                                                       | admin      | Manually check the details of a smart card or a pass.                                          |
| /smartcard revenue [agencyID]                                                                      | admin      | See revenue information of the specified transit operator.                                     |
| /smartcard withdraw [agencyID] [moneyAmount] [adminName]                                           | admin      | Transfer money from a transit operator's revenue to an admin's personal balance.               |
## Permissions
- "Smartcard.Admin": Gives access to admin commands. (OP is not sufficient, permission must be explicitly granted to admins)
	- Currently all commands are admin-use only, so granting this permission allows access to all commands of this plugin.

# Configuration
Server admins can define transit operators and customize each one in the config.yml file. There must be at least one transit operator.

Certain features can be disabled. If so, then the corresponding options in the self-service machine menu won't be displayed.
## config.yml File Contents
```
agencyID 1 (there must be at least one transit operator defined):
- company name 
- money currency exchange rate relative to Vault plugin's money (1 for default; must be >0)
- smart card config:
	- enabled (if false, it means this company doesn't offer smart cards, and functions 2.1.3 and 2.1.4 won’t appear in the self-service machine menu)
	- card brand name
	- card initial purchase cost (can be free)
	- card maximum load balance
	- penalty fee (for re-entering or re-exiting)
	- default card expiry time after the last transaction
	- allowed use in other companies (list; leave blank if none)
- single-journey ticket config:
	- enabled (if false, it means this company doesn't offer single-journey tickets, and function 2.1.1 won’t appear in the self-service machine menu)
	- default expiry time
- exit-only ticket config:
	- enabled (if false, it means this company doesn't offer exit-only tickets, and function 2.1.6 won’t appear in the self-service machine menu)
	- surcharge for getting one (usually set to the highest possible fare in the transportation system)
	- expiry time
- pass config:
	- enabled (if false, it means this company doesn't offer passes, and function 2.1.2 won’t appear in the self-service machine menu)
	- pass types:
		- passTypeID 1 (optional; there can be no passTypes defined for a transit operator):
			- name of the passType
			- pass purchase cost
			- expiry time
			- allowed number of uses (-1 for unlimited use)
			- valid stations/zones (in a list; leave blank if no restrictions)
			- allowed use in other companies (list; leave blank if none)
		- passTypeID 2:
			- ... same as above
- self-service machine config:
    - smart card add-value amount increments (list, max 12 options; default only $1 option)
- gate config:
	- gate open idle time (must be >0)
- fare calculation config:
    - station/zone fare chart filename (must be a csv table file; put the file in the designated FareCharts directory)
    - fare multiplier value (optional; can be 0 or above; 0 for free travel across the transportation network; >0 but <1 for a network-wide discount; >1 for a fare increase)

agencyID 2:
- ... same as above
```

## config.yml Example:
```
# You can define transit agencies in this file.  
# There must be at least one transit agency.  
  
# Notes:  
# For time durations, you must use this format: "amount unit"  
# - MUST leave a space between the amount and the unit  
# - the unit can be: "seconds" or "minutes" or "hours" or "days" or "weeks"  
# - CANNOT USE months or years as the unit since they vary in length  
# - the unit MUST BE in its full, plural form, even though if it is a 1  
# Accepted example: "1 seconds"  
# Unaccepted examples: "1 second" or "1 sec" or "1 secs" or "1 s", etc.  
  
# Transit agency example 1: Hong Kong's MTR  
# All features are enabled for demonstration  
MTR:  
  company-name: "MTR"  
  currency-exchange-rate: 1.0  
  smart-card-config:  
    enabled: true  
    card-brand-name: "Octopus Card"  
    purchase-cost: 50.0  
    max-balance: 3000.0  
    penalty-fee: 60.8  
    card-expiry-from-last-use: "90 days"  
    use-in-other-companies:  
      - "MTR_LRT"  
  single-journey-ticket-config:  
    enabled: true  
    expiry: "24 hours"  
  exit-only-ticket-config:  
    enabled: true  
    purchase-cost: 66.0  
    expiry: "7 minutes"  
  pass-config:  
    enabled: true  
    pass-types:  
      1:  
        pass-type-name: "Tuen Mun-Nam Cheong Day Pass"  
        purchase-cost: 30.0  
        expiry: "24 hours"  
        trips-allowed: -1  
        valid-stations:  
          - "TUM"  
          - "SIH"  
          - "TIS"  
          - "LOP"  
          - "YUL"  
          - "KSR"  
          - "TWW"  
          - "MEF"  
          - "NAC"  
        use-in-other-companies:  
          - "MTR_LRT"  
      2:  
        pass-type-name: "Tourist Day Pass"  
        purchase-cost: 75.0  
        expiry: "24 hours"  
        trips-allowed: -1  
        valid-stations: []  
        use-in-other-companies:  
          - "MTR_LRT"  
      3:  
        pass-type-name: "Sheung Shui / Wu Kai Sha - East Tsim Sha Tsui Monthly Pass"  
        purchase-cost: 510.0  
        expiry: "30 days"  
        trips-allowed: -1  
        valid-stations:  
          - "SHS"  
          - "FAN"  
          - "TWO"  
          - "TAP"  
          - "UNI"  
          - "FOT"  
          - "SHT"  
          - "TAW"  
          - "KOT"  
          - "MKK"  
          - "HUH"  
          - "WKS"  
          - "MOS"  
          - "HEO"  
          - "TSH"  
          - "SHM"  
          - "CIO"  
          - "STW"  
          - "CKT"  
          - "HIK"  
          - "DIH"  
          - "KAT"  
          - "SUW"  
          - "TKW"  
          - "HOM"  
          - "ETS"  
        use-in-other-companies: []  
  self-service-machine-config:  
    smart-card-add-value-amounts:  
      - 1.0  
      - 2.0  
      - 5.0  
      - 10.0  
      - 20.0  
      - 50.0  
      - 100.0  
  gate-config:  
    gate-open-time: "2 seconds"  
  fare-calculation-config:  
    fare-chart: "mtr_sample.csv"  
    fare-multiplier-value: 1.0  
  
# Transit agency example 2: Hong Kong's Light Rail ("subsidiary" of MTR)  
# Note that certain features can be disabled, as seen below  
MTR_LRT:  
  company-name: "MTR Light Rail"  
  currency-exchange-rate: 1.0  
  smart-card-config:  
    enabled: false  
    card-brand-name: ""  
    purchase-cost: 0.0  
    max-balance: 0.0  
    penalty-fee: 0.0  
    card-expiry-from-last-use: ""  
    use-in-other-companies: []  
  single-journey-ticket-config:  
    enabled: true  
    expiry: "24 hours"  
  exit-only-ticket-config:  
    enabled: false  
    purchase-cost: 0.0  
    expiry: ""  
  pass-config:  
    enabled: false  
    pass-types: []  
  self-service-machine-config:  
    smart-card-add-value-amounts:  
      - 1.0  
      - 2.0  
      - 5.0  
      - 10.0  
      - 20.0  
      - 50.0  
      - 100.0  
  gate-config:  
    gate-open-time: "2 seconds"  
  fare-calculation-config:  
    fare-chart: "mtr_lrt_sample.csv"  
    fare-multiplier-value: 1.0  
  
# Another example: the STM in Montreal, Quebec, Canada  
STM:  
  company-name: "Montreal Metro"  
  currency-exchange-rate: 5.68  
  smart-card-config:  
    enabled: false  
    card-brand-name: ""  
    purchase-cost: 0.0  
    max-balance: 0.0  
    penalty-fee: 0.0  
    card-expiry-from-last-use: ""  
    use-in-other-companies: []  
  single-journey-ticket-config:  
    enabled: false  
    expiry: ""  
  exit-only-ticket-config:  
    enabled: false  
    purchase-cost: 0.0  
    expiry: ""  
  pass-config:  
    enabled: true  
    pass-types:  
      1:  
        pass-type-name: "10-trip, All Modes A"  
        purchase-cost: 35.0  
        expiry: "365 days"  
        trips-allowed: 10  
        valid-stations:  
          - "A"  
        use-in-other-companies:  
          - "REM"  
          - "EXO"  
      2:  
        pass-type-name: "Monthly, All Modes A"  
        purchase-cost: 110.0  
        expiry: "30 days"  
        trips-allowed: -1  
        valid-stations:  
          - "A"  
        use-in-other-companies:  
          - "REM"  
          - "EXO"  
  self-service-machine-config:  
    smart-card-add-value-amounts: []  
  gate-config:  
    gate-open-time: "2 seconds"  
  fare-calculation-config:  
    fare-chart: ""  
    fare-multiplier-value: 0.0
```


# Plugin Requirements
## Supported Minecraft versions
- Developed and tested for Minecraft 1.21.11.
- All functionality should work on newer versions, but not necessarily for older versions.
## Dependencies
- Vault
