# NEOApp
Java application to get a list of “Near Earth Objects” using the NASA RESTful Web Service https://api.nasa.gov/api.html#NeoWS. Identify which NEO is the largest in size and which is the closest to Earth.
 
Outputs the total number of NEOs, and the details retrieved for both the largest and closest NEOs identified as of today.

RESTful web service used:
GET https://api.nasa.gov/neo/rest/v1/feed?start_date=START_DATE&end_date=END_DATE&api_key=API_KEY
where start_date = end_date = today's date.


## Getting Started

Clone the repository. Main class is defined in NEOApp.java file.

### Prerequisites

This application uses javax.json package downloaded as javax.json-1.1.jar to parse JSON data.  


## Running the tests

Run the application. The expected output is: 
- total number of NEOs returned from the RESTful web service
- Details of the biggest NEO
- Details of the closest to the Earch NEO.
- In addition, the application prints raw JSON response for debugging purpose.

## Author

* **Yulia Moldavsky** - *Initial work* - [PurpleBooth](https://github.com/yuliamsky)

