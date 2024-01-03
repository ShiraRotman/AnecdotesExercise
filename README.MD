Evidence Collector
------------------
How to run the service:
1. Install Maven (https://maven.apache.org/), making sure it's added to the path environment variable.
2. Navigate to project's folder.
3. Use 'mvn install' to build the project.
4. Navigate to the /target subfolder.
5. Use 'java -jar AnecdotesExercise-1.0.jar'.
(You also must have Java8+ installed)
Alternatively, you can build and run it in IntelliJ IDEA.

How to use the service:
The service runs on port 8080 and accepts POST requests with a JSON body.
The JSON format is:
{
    "evidencePayload":
    {
        This is the payload as was defined in the exercise, i.e:
        "evidence_id": 1,
        "evidence_data": ...
    },
    "evidenceFormats":
    [
        This is the parsing configuration for the payload. It consists of an array of objects in the format:
        {
            "resultFieldName": The name of the field in the result.
            "resultFieldFormat": This is a string which may contain JSON paths to payload fields from which to compose
            the result value. Each such path is identified by encompassing it with "${}". All other characters in the 
            string are copied to the final result.
        }
        
        For example:
        {
            "resultFieldName": "Full Name",
            "resultFieldFormat": "${user_details.first_name} ${user_details.last_name}"
        }
        There are 2 paths here which will be replaced with the corresponding field values from the payload.
        The space separating them will be included as-is.
    ]
}
The response will be a JSON consisting of the result fields and values as were defined in the parsing configuration.

About step 3:
-------------
If the payload is very large, then of course reading all objects to memory and processing the result in one bulk 
is not applicable.
A possible solution could be using (compressed) streams to read each object, process it and write the result object. 
This also requires the parsing configuration (which I assume will always be small enough to keep in memory) to be read 
separately from the payload.