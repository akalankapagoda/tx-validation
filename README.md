# Transaction Validation

Accepts a list of transactions as a CSV or a JSON and validates them.

Transactions are validated according to the below rules
- All transaction references should be unique
- Per record the end balance needs to be correct given the start balance and mutation

## The service exposes following endpoints

### GET /transaction/validation/hello

#### Response
Hello! The validation service is up and running!

### POST /transaction/validation

#### Request Parameters
- file - The input file as a MultipartFile
- type - One of 'CSV' or 'JSON'
- identifier - An identifier for the file

#### Response
    
    {
        "status": "SUBMITTED",
        "message": null,
        "identifier": "<identifier provided in the request>",
        "invalidRecords": []
    }

### GET /transaction/validation

#### Request Parameters

- identifier - The identifier used in submitting the validation file

#### Response

    
    {
        "status": "SUCCESS",
        "message": null,
        "identifier": "testJson",
        "invalidRecords": [
            {
                "reference": "<an invalid transaction reference>",
                "description": "<descriptino of the invalid transaction>"
            },
            ....
        ]
    }