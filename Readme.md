# Advertisement reports collector
This service reads reports from csv files at startup, saves to local
postgres and renders statistics.

## Run
### In docker
Ensure you have your report data in `./data`

    sudo ./gradlew build docker -x test -x test_integration
    sudo docker-compose up -d

### In OS
Ensure you have postgres available.

    export RESOURCE_PATH=/path/to/your/reports
    export POSTGRES_HOST=localhost:5432
    ./gradlew bootRun

## Improvements:
* hot loading csv's (not only at startup)
* ftp and s3 as paths for csv's
* graphql for getting statistics

## Protocol:

`/reports/<month>/<site>` - get report by month and site.
Where:
__month__ is a month of report. Can be a number `1-12`, or 3-letter code,
`Jan`, `Dec` or a full case-insensitive name `january`, `December`.
__site__ is a type or report. Available types: `desktop_web`,
`mobile_web`, `android`, `iOS`.
Response:
```
{
    "site": "desktop web",
    "requests": 12483775,
    "impressions": 11866157,
    "clicks": 30965,
    "reportData": "2018-01-01",
    "conversions": 7608,
    "ctr": 0.26,
    "cr": 0.06,
    "fillRate": 95.05,
    "ecpm": 1.99,
    "revenue (USD)": 23555.46,
    "month": "January"
}
```

`/reports/<month>?site=<site>&year=<year>` - previous request in another format.
Where:
__year__ year can be specified to retrieve reports by year-month pair.
_Optional_: default: `2018`
__site__ here site is optional. If not specified - aggregate report for all
sites of this month will be returned.
__month__ can be skipped if site is specified. Aggregate for all months
of this site will be returned.
Aggregate response for January:
```
{
    "requests": 34853988,
    "impressions": 33100001,
    "clicks": 86982,
    "reportData": "2018-01-01",
    "conversions": 21406,
    "ctr": 1.05,
    "cr": 0.25,
    "fillRate": 379.79,
    "ecpm": 7.88,
    "revenue (USD)": 65411.76,
    "month": "January"
}
```
Aggregate response for Desktop:
```
{
    "site": "desktop web",
    "requests": 23727650,
    "impressions": 22232512,
    "clicks": 71421,
    "reportData": null,
    "conversions": 9064,
    "ctr": 0.65,
    "cr": 0.07,
    "fillRate": 187.25,
    "ecpm": 3.51,
    "revenue (USD)": 39300.78,
    "month": null
}
```