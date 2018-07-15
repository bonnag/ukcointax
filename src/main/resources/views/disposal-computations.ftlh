<html>
    <head>
        <link rel="stylesheet" href="https://unpkg.com/purecss@1.0.0/build/pure-min.css" integrity="sha384-nn4HPE8lTHyVtfCBi5yW9d20FjT8BJwUXyWZT9InLYax14RDjBj46LmSztkmNP9w" crossorigin="anonymous">
        <style type="text/css">
table {  
    color: #333;
    font-family: Helvetica, Arial, sans-serif;
    table-layout: fixed;
    width: 25cm; 
    border-collapse: 
    collapse; border-spacing: 0; 
}

td, th {
  border: 1px solid #CCC;
  padding: 3px;
  width: 4cm;
}

td.double-width, th.double-width {
  width: 8cm;
}

td.full-width, th.full-width {
  width: 24.5cm;
}

th {
    background: #F3F3F3;
    font-weight: bold;
    text-align:left;
}

td {  
    background: #FAFAFA;
    text-align: right;
}
        </style>
    </head>
    <body>
        <table>
        <#list disposals as disposal>
            <tr>
              <th>Disposal #</th><td>${disposal.number}</td>
              <th>Disposal Date</th><td class="double-width" colspan="2">${disposal.date}</td>
              <th>&nbsp;</th>
            </tr>
            <tr>
              <th>Asset</th><td>${disposal.asset}</td>
              <th>Asset Type</th><td class="double-width" colspan="2">${disposal.assetType}</td>
              <th>&nbsp;</th>
            </tr>
            <tr>
              <th>Asset Amount</th><td>${disposal.assetAmount}</td>
              <th>Proceeds</th><td>${disposal.proceeds}</td>
              <th>Allowable Cost</th><td>${disposal.allowableCost}</td>
            </tr>
            <tr>
              <th>Identifications</th>
              <th>Rule</th><th>Acquisition Date</th><th>Asset Amount</th><th>Allowable Cost</th>
              <th>&nbsp;</th>
            </tr>
            <#list disposal.identifications as identification>
            <tr>
              <th>&nbsp;</th>
              <td>${identification.rule}</td><td>${identification.acquisitionDate}</td><td>${identification.assetAmount}</td><td>${identification.allowableCost}</td>
              <th>&nbsp;</th>
            </tr>
            </#list>
            <tr><th colspan="6" class="full-width">&nbsp;</th></tr>
        </#list>
        </table>
    </body>
</html>
