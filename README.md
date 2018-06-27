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
** margin trading;
** airdrops or chain splits / forks;
** trading fees that are in a currency that is not part of the trade - e.g. fees paid in AURA tokens on a OMG/ETH trade.
* ukcointax does not endorse any products or websites mentioned on these web pages. ukcointax advises you to do your own research and consult a tax professional.

## How to use ukcointax

Three basic steps:

1. Prepare records of your trades, either from your trading software, or by downloading from exchanges, perhaps with tools like https://cointracking.info/ or https://bitcoin.tax/.
2. Zip up your trades and upload them to the ukcointax cloud service. You will receive back a tax report. Or use the offline ukcointax app to analyse your trades and generate the report entirely on your computer.
3. Read the report.

## Preparing records

TODO - explain trade formats supported
TODO - explain why need exchange rates
TODO - explain exchange rate formats supported
TODO - give example input

## Generating a Report

### Cloud Service
Once you've prepared your trade records, Zip them up into a zip file and upload them using the page at TODO. You will receive a download link to the ukcointax report for your trades. The report will be a zip file containing CSV files.

The uploaded file and report will be deleted after one hour - ukcointax promises not to keep them or share them. The download link is publicly accessible during the one hour of existence but the link should be un-guessable to others.

### Offline App
The offline app is currently only recommended for software developers.

Usage:
java -jar ukcointax.jar -i inputDir -o outputDir

e.g.
java -jar ukcointax.jar -i C:/Users/Me/Documents/trades -o C:/Users/Me/Documents/taxreport

By default ukcointax in offline mode will not connect to the internet. You can however tell it to retrieve additional updated exchange rates from ukcointax servers by adding --download-rates to the command-line.

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

