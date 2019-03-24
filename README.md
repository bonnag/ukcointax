# ukcointax
Calculate UK Capital Gains Tax on Cryptocurrency trades

## Disclaimers

* ukcointax software is provided "as is", without warranty of any kind, express or implied, including but not limited to the warranties of merchantability, fitness for a particular purpose and noninfringement. In no event shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an action of contract, tort or otherwise, arising from, out of or in connection with the software or the use or other dealings in the software.
* ukcointax was not created by an accountant or a tax professional, nor has it been reviewed by an accountant, tax professional, or by HMRC. ukcointax recommends consulting a tax professional to discuss your circumstances.
* ukcointax attempts to calculate capital gains tax using the guidance at https://blocktax.uk/guide/ as of May 2018, which in turns appears to be based on HMRC guidance, but this guidance could be incorrect, out-of-date, or incorrectly applied by ukcointax.
* ukcointax is intended to calculate personal capital gains tax, but capital gains tax may not apply to your trading. See e.g. https://www.deciphertax.co.uk/ for some of the uncertainty around this.
* ukcointax effectively ignores profits or losses on fiat-to-fiat trades (e.g. selling Euros for Sterling), on the assumption these were incidental - e.g. crypto profits were sold for Euros, then the Euros were shortly sold for Sterling. This is likely not appropriate if making money from fiat-to-fiat trading was part of your trading strategy.
* ukcointax assumes you have given it a complete and accurate record of all your trades for all assets and all exchanges, including _all_ trades prior to the first tax year you are interested in, and all trades for at least the next 30 days after the end of the last tax year you are interested in. Missing trades can dramatically alter the reported figures due to identification rules.
* ukcointax assumes that all the cryptocurrencies traded are to be treated essentially like company shares. This may not be appropriate for stablecoins like Dai, and is not appropriate for non-fungible assets like cryptokitties.
* ukcointax does not have any support for:
  * margin trading;
  * airdrops or chain splits / forks;
  * trading fees that are in a currency that is not part of the trade - e.g. fees paid in AURA tokens on a OMG/ETH trade.
* ukcointax does not endorse any products or websites mentioned on these web pages. ukcointax advises you to do your own research and consult a tax professional.

## How to use ukcointax

Three basic steps:

1. Prepare records of your trades, either from your trading software, or by downloading from exchanges, perhaps with tools like https://cointracking.info/ or https://bitcoin.tax/.
2. Run the offline ukcointax app on your records to analyse your trades and generate the report entirely on your computer.
3. Read the report.

## Preparing records

### Trade Format ###

ukcointax needs records of your trades in a standard format.

The ukcointax native trade format is a CSV file with the following columns:

Column | Meaning | Example
---- | ---- | ----
"TradedAt" | Time the trade occurred. Must be in UTC in ISO-8601 format to millisecond precision. | 2017-12-30T08:01:00.000Z
"TradeSide" | Did you buy or sell the base asset? | Buy
"AssetBase" | The base asset | BTC
"AmountBase" | The amount of the base asset bought or sold | 1.00
"AssetQuoted" | The quoted asset (sometimes called counter currency - the price is quoted in this) | USD
"AmountQuoted" | The amount of the quoted asset bought or sold | 1.00
"Venue" | Name of the exchange on which the trade took place | Binance
"TradeId" | Unique identifier for the trade on this exchange | 857fjhru-358454gf-1235484

The names of the CSV files aren't important - you can have many CSV files of trades.

TODO - currently ukcointax doesn't have any special support for trading fees - you'll probably want to adjust the amount base or amount quoted to take into account the actual amount paid / received after fees are taken into account.

### Exchange Rate Format ###

Unless all your trades involved GBP, ukcointax will need historic exchange rates to estimate the value of the assets bought or sold.

For example, if you traded BTC and USD on the 12th Feb, you probably want to give ukcointax a USD/GBP exchange rate for that date.

The ukcointax native exchange rate format is a CSV file with the following columns:

Column | Meaning | Example
---- | ---- | ----
"QuotedAt" | Time the exchange rate was observed. Must be in UTC in ISO-8601 format to millisecond precision. | 2017-12-30T08:01:00.000Z
"AssetBase" | The base asset | GBP
"AssetQuoted" | The quoted (or counter) asset | USD
"Price" | How much is 1.0 of the base asset worth in the quoted asset? | 1.30

ukcointax will use the most up-to-date exchange rate you give it before the trade date - so if you gave it a USD/GBP exchange rate for the 10th Feb, it would use that for your trade on the 12th Feb.

ukcointax can triangulate across currencies to get a GBP value if needed - for example, if you have a BTC/ETH trade, and you have ETH/EUR and EUR/GBP exchange rates, it can use those rates to estimate the value of the ETH traded in GBP.

## Generating a Report

Once you've prepared your trade records and exchange rates, run the ukcointax app to get your reports.

### Cloud ###

TODO - ukcointax cloud service

### Offline Java App ###

TODO - Currently it's not very user-friendly ...

First, you'll need to install a Java run-time - e.g. from https://www.oracle.com/technetwork/java/javase/downloads/index.html .

Then, you'll need to download the ukcointax.jar.

You'll need to tell ukcointax.jar which directory your records are in, plus which directory you want it to write reports to.

It's a command-line app, so on Windows you'll need to hit start, then type Command Prompt.

Command-line Usage:
```
java -jar Downloads/ukcointax.jar -i inputDir -o outputDir
```

For example, if you've put your trades in C:/Users/Me/Documents/trades, then run:

```
java -jar Downloads/ukcointax.jar -i Documents/trades -o Documents/taxreport1
```

ukcointax will create the output directory and place the report csv files there.

## Common Problems

```
'java' is not recognized as an internal or external command
```

Did you install java? Is it in your PATH?

```
Error: Could not find or load main class ukcointax-0.2.jar
```

Did you forget the -jar bit?

```
Error: Unable to access jarfile ukcointax-0.2.jar
```

Are you running it where you downloaded it?

```
Exception in thread "main" java.io.IOException: directory Foo already exists, refusing to overwrite
```

To avoid accidents or confusion, it insists on creating a new output directory each time.

```
Exception in thread "main" java.lang.IllegalStateException: balance went negative at BalanceSnapshot{snapshotAt=2017-12-28T12:00:00Z, assetAmount=BTC -0.25}
```

This is a common and awkward problem. ukcointax computes running balances and has spotted that you appear to be spending assets it doesn't know you have.

ukcointax allows the GBP balance to go negative, but complains if any others do.

If the problem is a non-GBP fiat currency (say EUR), you could probably work around it by add a opening-positions.csv file containing an initial trade to represent buying your starting EUR - fiat-fiat trades are not reported by ukcointax.

## Reading the Report

Two main files to look at:

* errors-and-warnings.csv
* tax-summaries.csv

Other files allow calculations to be checked, anomalies to be spotted:

* valued-trades.csv
* disposals.csv
* identifications.csv
* inferred-balances.csv
* pool-history.csv

TODO - Example Report

# How it works

TODO - things to cover ...
* Sanity checking
* Inferred balances
* Appraising Trade Values
* Summing by day
* Identifying disposals with acquisitions
* Same day identifications
* Bed and breakfast identifications
* S104 pool identifications
* Day disposals
* Sum by year

# Licensing and Pricing
Source code of ukcointax is available under the terms of the AGPLv3 license.

There is no charge for using ukcointax in offline mode or cloud mode, though if you've found it useful and want to help it stay around next tax year, we'd appreciate a donation - TODO - details.

The ukcointax cloud service is intended only for non-automated use to prepare personal taxes.

If you'd like to use ukcointax source code or binaries in a way not covered by the AGPLv3, or use the ukcointax cloud service in a way not permitted above, or require support or consultancy, then do contact us - we can likely arrange a suitable license deal.

# Contact Us

TODO

