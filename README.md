![Design](https://github.com/notna4/msa-project/blob/main/app/sampledata/Frame%201%20(2).png?raw=true)
![Diagram](https://github.com/notna4/msa-project/blob/main/app/sampledata/MSAProjectDiagram%20(2).drawio.png?raw=true)
![Diagram](https://github.com/notna4/msa-project/blob/main/app/sampledata/MSA-UML-Diagram.drawio.png?raw=true)


```json
{
    "alarms": {
        "dimineataOra6": {
            "pasword": "asdaseqwjewd",
            "members:" {
                "cristi@gmail.com": true, // vrei sa iti sune alarma asta
                "laur&gmail.com": false, // nu vrei sa iti sune alarma asta
            },
            "hour": 6,
            "minutes": 6,
            "daysToRing": ["Mo", "Tu", "We"],
            "statistics": {
                "last7days": [
                    "laur&gmail.com",
                    "laur&gmail.com",
                    "laur&gmail.com",
                    "laur&gmail.com",
                    "cristi@gmail.com",
                    "laur&gmail.com",
                    "laur&gmail.com"
                ],
                "avgTimeInSeconds": {
                    "cristi@gmail.com": [2.1, 7], // prima valoare e timpul mediu, a doar valoare e numarul de cate ori m am jucat
                    "laur&gmail.com": [1.98, 7]
                }
            },
            "nextGame": {
                "DESCENDING": [[1, 8, 6], [4, 9, 2], [3, 5, 7]]
                // "ADDITION": [6, 9, 15] // 6+9=15
            }
        }
    }
},
{
    "accounts": {
        "cristi@gmail.com": {
            "username": "cristi",
            "password": "asdbqwe",
        },
        "laur@gmail.com": {
            "username": "laura",
            "password": "asdbqasdawe",
        }
    }
},

POST: createAlarm
{
    "id": "asldkjasdjkajs",
    "nameAlarm": "dimineataOra6",
    "password": "asdaseqwjewd",
    "hour": 6,
    "minutes": 6,
    "daysToRing": ["Mo", "Tu", "We"],
    "members:" {
        "cristi@gmail.com": true, 
        "laur&gmail.com": false,
    },
}

POST editAlarm (nameAlarm: "dimineataOra6", id: "asldkjasdjkajs")
{
    "nameAlarm": "dimineataOra7Jum",
    "password": "sddsda",
    "hour": 7,
    "minutes": 30,
    "daysToRing": ["Mo", "Tu", "We", "Fri"], 
    "members:" {
        "cristi@gmail.com": true, 
        "laur&gmail.com": true,
        "asdas@mail.com": false,
    },
}

GET getAlarm  (nameAlarm: "dimineataOra6", id: "asldkjasdjkajs")

POST createAccount
```
