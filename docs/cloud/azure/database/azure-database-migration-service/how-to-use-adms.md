---
title: Azure Database Migration Service
description: Not every tool is appropriate for every task, and the data migration tools in Azure are no different. 
ms.assetid: 4957903f-d250-4fe9-8ccc-5694d5a57928
ms.topic: how-to
ms.date: 06/11/2021
ms.custom: ""
ms.services: "azure-database-migration"
ms.author: "bcampbell"
_tocTitle: "Azure"
---


<!-- divi:html -->
<!--
 /* Font Definitions */
 @font-face
	{font-family:Wingdings;
	panose-1:5 0 0 0 0 0 0 0 0 0;}
@font-face
	{font-family:"Cambria Math";
	panose-1:2 4 5 3 5 4 6 3 2 4;}
@font-face
	{font-family:Calibri;
	panose-1:2 15 5 2 2 2 4 3 2 4;}
@font-face
	{font-family:"Segoe UI Emoji";
	panose-1:2 11 5 2 4 2 4 2 2 3;}
@font-face
	{font-family:"Segoe UI";
	panose-1:2 11 5 2 4 2 4 2 2 3;}
 /* Style Definitions */
 p.MsoNormal, li.MsoNormal, div.MsoNormal
	{margin-top:0in;
	margin-right:0in;
	margin-bottom:10.0pt;
	margin-left:0in;
	line-height:115%;
	font-size:11.0pt;
	font-family:"Calibri",sans-serif;}
h1
	{mso-style-link:"Heading 1 Char";
	margin-top:12.0pt;
	margin-right:0in;
	margin-bottom:0in;
	margin-left:0in;
	margin-bottom:.0001pt;
	line-height:115%;
	page-break-after:avoid;
	font-size:16.0pt;
	font-family:"Cambria",serif;
	color:#365F91;
	font-weight:normal;}
a:link, span.MsoHyperlink
	{color:blue;
	text-decoration:underline;}
a:visited, span.MsoHyperlinkFollowed
	{color:purple;
	text-decoration:underline;}
p.MsoListParagraph, li.MsoListParagraph, div.MsoListParagraph
	{margin-top:0in;
	margin-right:0in;
	margin-bottom:10.0pt;
	margin-left:.5in;
	line-height:115%;
	font-size:11.0pt;
	font-family:"Calibri",sans-serif;}
p.MsoListParagraphCxSpFirst, li.MsoListParagraphCxSpFirst, div.MsoListParagraphCxSpFirst
	{margin-top:0in;
	margin-right:0in;
	margin-bottom:0in;
	margin-left:.5in;
	margin-bottom:.0001pt;
	line-height:115%;
	font-size:11.0pt;
	font-family:"Calibri",sans-serif;}
p.MsoListParagraphCxSpMiddle, li.MsoListParagraphCxSpMiddle, div.MsoListParagraphCxSpMiddle
	{margin-top:0in;
	margin-right:0in;
	margin-bottom:0in;
	margin-left:.5in;
	margin-bottom:.0001pt;
	line-height:115%;
	font-size:11.0pt;
	font-family:"Calibri",sans-serif;}
p.MsoListParagraphCxSpLast, li.MsoListParagraphCxSpLast, div.MsoListParagraphCxSpLast
	{margin-top:0in;
	margin-right:0in;
	margin-bottom:10.0pt;
	margin-left:.5in;
	line-height:115%;
	font-size:11.0pt;
	font-family:"Calibri",sans-serif;}
p.MsoIntenseQuote, li.MsoIntenseQuote, div.MsoIntenseQuote
	{mso-style-link:"Intense Quote Char";
	margin-top:.25in;
	margin-right:.6in;
	margin-bottom:.25in;
	margin-left:.6in;
	text-align:center;
	line-height:115%;
	border:none;
	padding:0in;
	font-size:11.0pt;
	font-family:"Calibri",sans-serif;
	color:#4F81BD;
	font-style:italic;}
span.IntenseQuoteChar
	{mso-style-name:"Intense Quote Char";
	mso-style-link:"Intense Quote";
	color:#4F81BD;
	font-style:italic;}
span.Heading1Char
	{mso-style-name:"Heading 1 Char";
	mso-style-link:"Heading 1";
	font-family:"Cambria",serif;
	color:#365F91;}
.MsoChpDefault
	{font-family:"Calibri",sans-serif;}
.MsoPapDefault
	{margin-bottom:10.0pt;
	line-height:115%;}
@page WordSection1
	{size:8.5in 11.0in;
	margin:1.0in 1.0in 1.0in 1.0in;}
div.WordSection1
	{page:WordSection1;}
 /* List Definitions */
 ol
	{margin-bottom:0in;}
ul
	{margin-bottom:0in;}
-->






<div class="WordSection1">


## Overview

As we continue to hurdle through the year, our mandate to move to the cloud becomes more and more pressing. So, it would seem prudent to attempt to utilize every available resource available. In this article, I will discuss the Azure Data Migration Service as it pertains to migrating a SQL Server to Azure SQL Database and to Azure SQL Managed instance. I will include links to other migration scenarios at the end of this document.

Not every tool is appropriate for every task, and the data migration tools in Azure are no different. The Data Migration Assistant (DMA) is good for smaller moves, meaning, just a few databases and moderate amounts of data. The Data Migration Service (DMS) is the tool you are looking for, if you have a large amount of data and many databases to migrate. It will use the DMA (where applicable) to assess the state of your cloud readiness for your on prem set up. This will allow you the opportunity to rationalize (I’m using the
term as it pertains to the [Cloud Adoption Framework](https://dojo.o360.cloud/2020/06/16/moving-to-the-cloud-saas-iaas-paas-faas-or-serverless-how-do-i-know-what-model-is-what-we-need-part-2-the-microsoft-cloud-adoption-framework/)) your work loads and prioritize what you intend to fix for migration and what you intend recreate in the cloud. Once you have made those decisions, the DMS can perform all the necessary steps. Theoretically, you could set up the project – hit the “GO” button and walk away. As, the DMS is programmed to follow all best practices…as determined by Microsoft. You can even do an online migration, however, that requires that your instance of the DMS is set up on the premium pricing tier. However, small to medium workloads are free up to 4 vcores and support only offline migrations. The starting rate for the premium compute (online migrations) is $0.37 per hour for 4 vcores.

## Database Migration Service Scenarios

As mentioned above, there are 2 main ways to use the DMS, Online and Offline. The chart below shows which source and target scenarios are available for both offline and online migrations. As you can see, using the DMS for Online migrations supports a more diverse array of potential source and target configurations. As, an offline migration requires an outage from the time the migration process starts to the time that it finishes. Since acceptable downtime will vary from SLA to SLA, it would be a good idea to try and determine if migration time plus cutover can fall within acceptable outage windows for your applications SLA’s. Not that you need to be told, but, the cells highlighted in green indicate which scenarios are supported. This chart was accurate as of July 8<sup>th</sup> , 2020. If one of the listed scenarios that you are interested in using is not currently supported, you can request participation in Microsoft’s Private Preview program (beta testing) via a submission at the [DMS Preview Site](https://aka.ms/dms-preview).

<table class="MsoNormalTable" border="0" cellspacing="0" cellpadding="0" width="571" style='width:428.0pt;margin-left:5.65pt;border-collapse:collapse'>
 <tr style='height:15.0pt'>
  <td width="571" colspan="4" style='width:428.0pt;border:solid windowtext 1.0pt;background:#BFBFBF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif'>Offline (one-time) migration support</span></b></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="571" colspan="4" style='width:428.0pt;border:solid windowtext 1.0pt;border-top:none;background:#BFBFBF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>The following table shows
  Azure Database Migration Service support for offline migrations.</span></b></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;background:#BFBFBF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Target</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#BFBFBF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Source</span></b></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#BFBFBF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Support</span></b></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#BFBFBF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Status</span></b></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Azure SQL DB</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>SQL Server</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>RDS SQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>X</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Oracle</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>X</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Azure SQL DB MI</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>SQL Server</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>RDS SQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>X</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Oracle</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>X</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Azure SQL VM</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>SQL Server</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Oracle</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>X</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Azure Cosmos DB</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>MongoDB</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Azure DB for MySQL</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>MySQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>X</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>RDS MySQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>X</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Azure DB for PostgreSQL - Single server</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>PostgreSQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>X</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>RDS PostgreSQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>X</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Azure DB for PostgreSQL - Hyperscale (Citus)</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>PostgreSQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>X</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>RDS PostgreSQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>X</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="571" nowrap colspan="4" valign="bottom" style='width:428.0pt;border:  solid windowtext 1.0pt;border-top:none;background:black;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="571" colspan="4" style='width:428.0pt;border:solid windowtext 1.0pt;border-top:none;background:#BFBFBF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Online (continuous sync)
  migration support</span></b></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="571" colspan="4" style='width:428.0pt;border:solid windowtext 1.0pt;border-top:none;background:#BFBFBF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>The following table shows
  Azure Database Migration Service support for online migrations.</span></b></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;background:#BFBFBF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Target</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#BFBFBF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Source</span></b></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#BFBFBF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Support</span></b></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#BFBFBF;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Status</span></b></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Azure SQL DB</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>SQL Server</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>RDS SQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Oracle</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>X</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Azure SQL DB MI</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>SQL Server</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>RDS SQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Oracle</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>X</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Azure SQL VM</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>SQL Server</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>X</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Oracle</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>X</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Azure Cosmos DB</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>MongoDB</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Azure DB for MySQL</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>MySQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>RDS MySQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Azure DB for PostgreSQL - Single server</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>PostgreSQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
 <tr style='height:.5in'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:.5in'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:.5in'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Azure DB for PostgreSQL - Single server*</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:.5in'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:.5in'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>RDS PostgreSQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
 <tr style='height:24.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:24.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:24.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Oracle</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:24.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:24.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Public preview</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><b><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>Azure DB for PostgreSQL - Hyperscale (Citus)</span></b></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>PostgreSQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
 <tr style='height:15.0pt'>
  <td width="335" valign="top" style='width:251.0pt;border:solid windowtext 1.0pt;border-top:none;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>&nbsp;</span></p>
  </td>
  <td width="107" valign="top" style='width:80.0pt;border-top:none;border-left:  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" style='margin-bottom:0in;margin-bottom:.0001pt;line-height:  normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>RDS PostgreSQL</span></p>
  </td>
  <td width="65" valign="top" style='width:49.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI Emoji",sans-serif;color:black'>&#10004;</span></p>
  </td>
  <td width="64" valign="top" style='width:48.0pt;border-top:none;border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;background:#C6E0B4;padding:0in 5.4pt 0in 5.4pt;height:15.0pt'>
  <p class="MsoNormal" align="center" style='margin-bottom:0in;margin-bottom:.0001pt;text-align:center;line-height:normal'><span style='font-size:9.0pt;font-family:"Segoe UI",sans-serif;color:black'>GA</span></p>
  </td>
 </tr>
</table>

## Prerequisites

As with all things Azure, there are going to be prerequisites that must be in place to successfully utilize the DMS.

> **Note:**
>
> Creating an instance of Azure Database Migration Service requires access to virtual network settings that are normally not within the same resource group. As a result, the user creating an instance of DMS requires permission at subscription level.

### Prequisites that are common across all migration scenarios, are as follows

1. A Microsoft Azure Virtual Network must be created for Azure Database Migration Service. It is suggested that this is done using the Azure Resource Manager deployment model. This will provide site-to-site connectivity between the on-prem source servers by utilizing either [ExpressRoute](https://docs.microsoft.com/en-us/azure/expressroute/expressroute-introduction) or [VPN](https://docs.microsoft.com/en-us/azure/vpn-gateway/vpn-gateway-about-vpngateways).

2. Within the virtual network, the Network Security Group (NSG) [rules need to be set](https://docs.microsoft.com/en-us/azure/virtual-network/virtual-networks-nsg) so that ports 443, 53, 9354, 445, 12000 are not blocked.

3. Firewall rules for the source instance/database must be set to allow the Azure Database Migration Service access.

4. Your Windows Firewall must be [configured to allow database engine access](https://docs.microsoft.com/en-us/sql/database-engine/configure-windows/configure-a-windows-firewall-for-database-engine-access).

5. TCP/IP must be [enabled](https://docs.microsoft.com/en-us/sql/database-engine/configure-windows/enable-or-disable-a-server-network-protocol#SSMSProcedure). If the source is an instance of SQL Server Express, TCP/IP is disabled by default.</p>

### Prerequisites for migrating SQL Server to Azure SQL Database

1. There must already be an instance of Azure SQL Database [created](https://docs.microsoft.com/en-us/azure/sql-database/sql-database-get-started-portal).

2. You must have the [DMA](https://www.microsoft.com/download/details.aspx?id=53595) (version 3.3 or higher) downloaded and installed.

3. The appropriate ports for both source and destination must be opened to allow the Azure DMS the appropriate access. For SQL Server, this is port 1433.

4. If the source server for the migration contains multiple named SQL Server instances (using dynamic ports), enable the SQL Browser Service and allow access to UDP port 1434 through your firewalls. This is so that the Azure Database Migration Service can connect to a named instance on your source server.

5. Create a server-level [firewall rule](https://docs.microsoft.com/en-us/azure/sql-database/sql-database-firewall-configure) for SQL Database to allow the Azure Database Migration Service access to the target databases.  
    1. Provide the subnet range of the virtual network used for the Azure Database Migration Service.

6. Ensure that the credentials used to connect to the source instances have the CONTROL SERVER permissions.

7. Ensure that the credentials used to connect to the target database has the CONTROL DATABSE permissions.

8. **A complete list of perquisites for an Offline migration can be found [here](https://docs.microsoft.com/en-us/azure/dms/tutorial-sql-server-to-azure-sql). Further, a complete list of perquisites for an Online migration can be found [here](https://docs.microsoft.com/en-us/azure/dms/tutorial-sql-server-azure-sql-online#prerequisites). I STRONGLY recommend that you review these lists.**

### Prerequisites for migrating SQL Server to Azure SQL Managed Instance

1. A SQL Managed Instance must be [created](https://aka.ms/sqldbmi)</a> in the Azure portal.

2. Open firewalls for SMB traffic (port 445) for the IP address/subnet range that the Azure Database Migration Service is using.

3. Open Windows Firewalls so the Azure Database Migration Service can access the source SQL Server (the default TCP port is 1433).

4. If your source server is running multiple SQL Server named instances (using dynamic ports), enable the SQL Browser Service and open access to UDP port 1434. This will allow the Azure Database Migration Service to connect to any of the named instances.

5. Any logins used to connect the source SQL Server and target Managed Instance must be members of the sysadmin server role.

6. The DMS will need a file share that is large enough to accommodate full a backup of each DB that is going to be migrated (it will delete the backup it creates after it completes its process). Have a network share that the Azure Database Migration Service can use to back up the source database.

7. Ensure that the SQL Server service account (that is running the source instance) has write privileges on the network share mentioned above and that the computer account for the source server has read/write access to the same share.

8. A Windows user (and password) that has full control privilege on the same network share will be used to upload the backup files to an Azure Storage container. The Azure Database Migration Service impersonates that user credential to operate the restore operation.

9. A blob container must be created. You will need its Shared Access Signature and URL. Create it using the steps in the article [Manage Azure Blob Storage resources with Storage Explorer](https://docs.microsoft.com/en-us/azure/vs-azure-tools-storage-explorer-blobs#get-the-sas-for-a-blob-container). Make sure that Read, Write, Delete, and List permissions are selected on the policy window when creating the SAS URI.

10. **A complete list of perquisites for an Offline migration can be found [here](https://docs.microsoft.com/en-us/azure/dms/tutorial-sql-server-to-managed-instance#prerequisites). Further, a complete list of perquisites for an Online migration can be found [here](https://docs.microsoft.com/en-us/azure/dms/tutorial-sql-server-managed-instance-online#prerequisites). I STRONGLY recommend that you review these lists.**

## Creating the Database Migration Service

The DMS Service can be created in several different ways and with two separate types. It can be created manually via the portal, it can be created using JSON (ARM Template), and (**this is the Optum preferred method**) it can be created using [TerraForm](https://www.terraform.io/docs/providers/azurerm/r/database_migration_service.html). It should be noted that no more than ten instances of the DMS can be created per subscription. To create more than that, a Microsoft support ticket must be created.

### General process

In order to create an instance of the DMS, you will need to:

1. Register the resource provider.
    1. Inside your subscription (inside of "All Services"), select resource providers.
    2. On the following page, search for "Migration".
       1. This should list Microsoft Data Migration as a provider. Selecting the register option on the right will complete this part of the process.

2. Create an instance of the DMS.
    1. Inside the same subscription, select "Resources"
       1. Select "Create a Resource"
       2. Search for "Migration"
       3. Azure Database Migration Service will be listed in your results.
          1. Select the "Create" option. If you are doing this in the portal, a dialog box will appear. It is here that you will enter the appropriate information to finalize the creation of the service.
             1. Choose a sensible name for your DMS
             2. Select your subscription
             3. Select an existing resource group (you can create a new one, but this just creates something else you will have to clean up when you are done)
             4. If possible, select a location that is closest to either your source system or your target system. One does not seem to be preferred to the other
             5. Select an existing Virtual Network. Again, you can create a new one (more to clean up…).
             6. Choose your pricing tier. As we discussed earlier, if it is your intention to do an on-line migration, you MUST select the premium pricing tier.
             7. Select "Create"
          2. After a few minutes, you will see confirmation that your instance has been created and is ready to use.

## Data Migration Service Hybrid Setup (In Preview)

Setting up an instance of the DMS in Hybrid mode is especially helpful when site to site connectivity between the on prem resource and Azure is an issue, or, if bandwidth is limited. There are a couple of things to take note of:

1. Azure Active Directory must be set up and running.

> **Note**
> While researching this option, I came across this note out in the portal and thought that it needed to be mentioned here. It appears that the current Azure Active Directory feature set is going away and will be the Microsoft Authentication Library going forward. **"Starting June 30th, 2020 we will no longer add any new features to Azure Active Directory Authentication Library (ADAL) and Azure AD Graph. We will continue to provide technical support and security updates but we will no longer provide feature updates. Applications will need to be upgraded to Microsoft Authentication Library (MSAL) and Microsoft Graph. Learn more"**

2. Currently, Azure Database Migration Service running in hybrid mode supports SQL Server migrations to:
    1. Azure SQL Managed Instance with near zero downtime (online),
    2. Azure SQL Database single database with some downtime (offline),
    3. MongoDb to Azure CosmosDB with near zero downtime (online),
    4. MongoDb to Azure CosmosDB with some downtime (offline).

3. The Azure DMS hybrid installer runs on Microsoft Windows Server 2012 R2, Window Server 2016, Windows Server 2019, and Windows 10, and it requires .NET 4.7.2 or later. The latest versions of .NET can be found on the [Download .NET Framework](https://dotnet.microsoft.com/download/dotnet-framework) page.

Setting up the Hybrid version of the DMS can be broken into several sections and includes the setup mentioned above. There a just a few
differences and separate steps

1. Register the resource provider. (Same as Above)

2. Create an instance of the DMS - This is also the same process as is mentioned above, with one exception. There is an option for "Service Mode" that is set to "Azure" by default. To utilize the Hybrid mode, you must select (you guessed it) the hybrid option
    1. As mentioned above, the process will process for a bit and then present you with notification that the service has been created and is ready for use.
        1. **This is important** - on the DMS page that you just created, select the properties option. Within the resource id box, select and copy the value. Paste it into notepad and keep it close by, as you will use this value when you install the Azure DMS Hybrid worker on the on-prem resource.

3. Create an Azure App Registration ID
    1. The DMS uses an APP ID to talk to Azure Services. The APP ID that needs to be created, will require either the Contributor role (Subscription level). Corporate security will not (typically) allow that. However, custom roles that grant the specific permissions that the DMS requires can be created. [Current Recommendations](https://docs.microsoft.com/en-us/azure/dms/resource-custom-roles-sql-db-managed-instance#minimum-number-of-roles)are that there be two custom roles created for your DMS App Id, one at the subscription level and one at the resource level.
    1. In the portal (This is a [link](https://github.optum.com/Dojo360/azure-active-directory-service-principal) to a TerraForm module in the DoJo that creates an Azure Active Directory Service Principal)
        1. Select Azure Active Directory &gt; select App registrations &gt; select New registration
            1. Create an appropriate application name - make it something that you will easily remember, you will use this ID as part of the hybrid worker install.
            1. Choose the single tenet account type - the other options make the app id visible to any Azure AD Directory (we don't want that…)
            1. Accept the defaults for the Redirect URL
            1. Click Register
        1. Navigate back to the Azure Database Migration Service section of the portal
            1. select Access control (IAM)
                1. select Add role assignment
                1. select the "contributor" role
                1. select "Azure AD user, group or service principal" in the Assign access to drop down
                1. select the App ID you created in the previous step
                1. save
                    1. This saves the role assignment for your App ID in the DMS

4. Download and install the hybrid worker
    1. Once an Azure Application Id has been created, you will need to download and install the hybrid worker on the server that will be hosting the hybrid worker. The download can be found out on the portal.
    1. Navigate to your instance of the DMS.
        1. Select the Hybrid option in the left pane
            1. Choose "Installer Download" on the top of the right pane
            1. The hybrid worker install is zipped and will need to be extracted on the box that will be hosting it.
        1. Once the zip file has been extracted, locate the dmsSettings.json file and open it for editing
            1. This is where you get to use the app and resource ID's you created earlier in the process. There are two sections that need your attention, they are the "Authentication" and "DMSService".
                1. In the "[Azure AD App Id]" placeholder, Insert the App Id that you created earlier
                1. In the "[Cloud DMS resource id]" placeholder, insert the previously resource Id.
                1. Save the file
        1. A certificate must be generated on the same box you are installing the hybrid worker on. the cert will be uploaded to the portal and be used to verify the connection between the on prem instance and the portal.
            1. Open a CMD window and execute the following command:
                1. ```&lt;drive&gt;:\&lt;folder&gt;\Install&gt;DMSWorkerBootstrap.exe -a GenerateCert```
            1. This will create the certificate in the install folder.
            1. In the portal, navigate to the certificates and secrets section under the mange section of the app id we created.
                1. Select "Upload Certificate"
            1. Your app id is now tied to the system that is going to host your hybrid worker and to the DMS in the portal.
        1. Now you are ready to install the hybrid worker on the host machine. As in creating the certificate, you will need to execute the following command:
            1. **Note the -p {InstallLocation} parameter can be added to alter the installation path which, by default, is "C:\Program Files\DatabaseMigrationServiceHybrid".**
            1. ```&lt;drive&gt;:\&lt;folder&gt;\Install&gt;DMSWorkerBootstrap.exe -a Install -IAcceptDMSLicenseTerms -d```
        1. Following a successful install, the DMS will display a status of "online" in the portal. You are now ready to create and run your migration project. When your migration is complete, you will want to uninstall the hybrid worker. This can only be done by executing the following command:
            1. ```&lt;drive&gt;:\&lt;folder&gt;\Install&gt;DMSWorkerBootstrap.exe -a uninstall```

## Migrating to Azure SQL Database (Offline/Online)

### Offline Migration to Azure SQL Database

#### Migrate the sample schema

1. [Assuming that you have used the DMA to assess your database](https://docs.microsoft.com/en-us/azure/dms/tutorial-sql-server-to-azure-sql#assess-your-on-premises-database), and that you're comfortable with the assessment and confident that your database can be migrated; use the DMA to migrate your database's schema to Azure SQL Database.
    1. In the DMA, select the New (+) icon, and then under Project type, select Migration.
    1. Name the project
        1. select SQL Server in the Source server type text box
        1. In the Target server type text box, select Azure SQL Database.
    1. Under Migration Scope, select Schema only.
    1. Select Create to create the project.
    1. In the DMA, enter the connection details for your source Db (SQL Server)
        1. select Connect
        1. Following a successful connection, you should see your Db(s) listed, select the Db that you intend to migrate.
    1. Choose Next
        1. Enter the target connection details for the Azure SQL Database under Connect to target server.
            1. This will include the server name, authentication type, and the appropriate credentials for that authentication type.
        1. Select Connect
        1. then select the Azure SQL Database you provisioned at the beginning of this process.
    1. Selecting Next will advance you to the Select objects screen. Here, you can specify the schema objects in your source database that you intend to deploy to Azure SQL Database.
        1. By default, all objects are selected
        1. Selecting "Generate SQL script" will create the SQL scripts
        1. Review the scripts for any errors. You can copy the scripts into an SSMS query window to do this and verify that they compile.
    1. Select "Deploy schema" to execute the scripts on the Azure SQL Database.
    1. Following the schema deployment, check your target server for any issues.
1. Create a migration project
    1. After the service is created, locate it within the Azure portal, open it, and then create a new migration project.
    1. Select All services In the Azure portal menu. Search for the DMS.
    1. On the DMS screen, choose the DMS instance that you created.
    1. Select New Migration Project.
    1. On the New DMS project screen,
        1. name the project,
        1. choose SQL Server as the Source server type,
        1. choose Azure SQL Database as the Target server type,
        1. select Offline data migration as the type of activity.
    1. Selecting "Create and run activity" will create the project and run the migration.
1. Specify source details
    1. On the DMS source detail screen, specify the connection details for the SQL Server source instance.
        1. This means that, where DNS name resolution is possible, use a Fully Qualified Domain Name (FQDN) as the name for Source instance name.
        1. The system's IP Address can be used in situations where DNS name resolution isn't possible.
    1. Earlier, we executed the "GenerateCert" command to install a trusted certificate on our source server, this allows us to check the Trust server certificate check box without having SQL Server generate a self-signed certificate. This is important because the self-signed cert creates a TLS connection that is weakly encrypted and not very secure. Further, they are vulnerable to "Man-In-The-Middle" attacks. Therefore, it is not a best practice to use a self-signed certificate on production servers or servers connected to the internet.
    1. Select Save.
1. Specify target details
    1. On the Migration target details screen, specify the connection details for the target Azure SQL Database, which is the pre-provisioned Azure SQL Database to which your Db schemas were deployed to using the Data Migration Assistant.
    1. Select Save
    1. Next is the Map to target databases screen. Select source and target databases. The target will be available via a dropdown on the same line as the source Db.
        1. If the target database is the same name as the source database, the DMS selects the target database of the same name by default.
    1. Select Save
    1. On the Select tables screen, you will want to expand the table listing. Each table is listed individually. The fields of the table are listed as TableName.FieldName, so you will want to review the list of affected fields.
        1. The DSM will auto select all the empty tables that are on the target Azure SQL Database. Remember, there will have been tables and data placed on this instance as part of the schema creation that was done via the DMA. If you want to migrate those tables again, you will need to navigate to those tables via this interface.
    1. Once you have completed your table selections, select Save.
    1. We are now to the point where you will see a summary of the migration.
        1. In the Activity name text box, name the migration. I suggest "PortfolioName_ServerName_Migration_Date". But, that's just me. At this time, I am unaware of an Optum naming convention that would apply to this activity. So, if one is established, I will augment this doc to reflect that.
        1. Expand the Validation option section to display the Choose validation options. You can then specify whether or not to validate the migrated databases. You have the option to validate via Schema comparison, Data consistency, Query correctness, or all the above. Remember, the size of the data matters. So, consider this when you are determining if or how much of your Db to validate.
    1. Select Save
    1. A Summary of the options you selected will be displayed. Make sure that the source and target details match what you specified.
    1. Select Run migration
1. Run the migration
    1. As you selected "Run Migration" in the previous step, the DMS activity window appears with the activity name (PortfolioName_ServerName_Migration_Date) and the Status of the activity. The status should be "Pending".
1. Monitor the migration
    1. On the DMS activity screen, selecting "Refresh" will update the status until the migration is complete.
    1. Following completion, you can download a report that will list all of the details of the migration process.
    1. Now, all that’s left to do is verify the target database(s) on the target Azure SQL Database.
1. Bring the application back online using the freshly migrated Azure SQL Database

## Online Migration to Azure SQL Database

The process of setting up and executing an online DB migration is almost the same as doing it offline (see above). However, If you have not gone through the complete list of prerequisites for both the [online](https://docs.microsoft.com/en-us/azure/dms/tutorial-sql-server-azure-sql-online#prerequisites) and [offline](https://docs.microsoft.com/en-us/azure/dms/tutorial-sql-server-to-azure-sql#prerequisites) DMS migrations, **NOW** would be an excellent time to do so. As, the differences mentioned here will be directly affected by some differences in the prerequisites.

There are 2 main differences in the set up and execution, but one big difference in the process.

### Difference 1

In section 2.a.4.d above, you are directed to choose "Offline" as your activity type. To perform an Online migration, choose "Online
data migration" as your activity type. This will require that these criteria be in place.

1. The source SQL Server must be 2005 or above.
1. Both source and target databases should be in either Full (this is my preference) or Bulk-logged recovery mode.
1. Make sure that you are getting full database backups.
1. If there are any tables that do not have a primary key will cause Chang Data Capture to be enabled for that database and the specific tables that do not have PK's.
1. The source database must be configured for Distributor role.

### Difference 2

With the offline migration, it is assumed that you have taken an outage window appropriate to the estimated amount of time needed to
migrate the Instance. With the Online option, following the completion of the initial full load, you will "Cutover" to the new instance. To this point, your application has not been offline. The following steps will replace sections six and seven above.

1. Monitor the Migration
    1. On the DMS activity screen, selecting "Refresh" will update the status until the migration is complete.
    1. You can click on each database to get to the migration status for both the "Full data load" and the "Incremental data sync" processes.
1. Perform Migration Cutover
    1. After the initial Full migration has completed, the migration details will have a status of "Ready to cutover".
        1. To complete the database migration, select Start Cutover.
        1. You will need to make sure to stop all incoming transactions to the source database
            1. The "Pending changes" counter will show 0. This will be visible in the right-hand pane of the DMS activity screen.
            1. Now, select "Confirm", and then select "Apply".
            1. When the database migration status bar shows Completed, connect your applications to the new target Azure SQL Database.

## Migrating to SQL Server Managed Instance (Offline/Online)

An Azure SQL Managed Instance supports two database migration options (currently):
- Azure Database Migration Service
- Native RESTORE DATABASE FROM URL

### Offline to SQL Server Managed Instance

1. Create a migration project
    1. After the service is created, locate it within the Azure portal, open it, and then create a new migration project.
        1. select All services In the Azure portal menu. Search for the DMS.
        1. On the DMS screen, choose the DMS instance that you created.
        1. Select New Migration Project.
        1. On the New DMS project screen,
            1. name the project, choose SQL Server as the Source server type,
            1. choose Azure Managed Instance e as the Target server type,
            1. select Offline data migration as the type of activity.
        1. Selecting "Create and run activity" will create the project and run the migration.
1. Specify source details
    1. On the DMS source detail screen, specify the connection details for the SQL Server source instance.
        1. This means that, where DNS name resolution is possible, use a Fully Qualified Domain Name (FQDN) as the name for Source instance name.
        1. The system's IP Address can be used in situations where DNS name resolution isn't possible.
    1. Earlier, we executed the "GenerateCert" command to install a trusted certificate on our source server, this allows us to check the Trust server certificate check box without having SQL Server generate a self-signed certificate. This is important because the self-signed cert creates a TLS connection that is weakly encrypted and not very secure. Further, they are vulnerable to  "Man-In-The-Middle" attacks. Therefore, it is not a best practice to use a self-signed certificate on production servers or servers connected to the internet.
    1. Select Save.
1. Specify target details
    1. Using the Migration target details screen in the DMS, specify the connection details for the target Azure SQL Managed Instance.
        1. If that isn't done, or you don't know how to do that, this [link](https://docs.microsoft.com/en-us/azure/sql-database/sql-database-managed-instance-get-started) from Microsoft might get you pointed in the right direction. However, remember to try and stick to the [Optum playbooks](/docs/azure/managed-sql) as closely as possible.
    1. Select Save.
1. Select source databases
    1. On the DMS select source databases screen, select the databases that you want to migrate.
    1. Select Save.
1. Select logins
    1. On the Select logins screen, select the logins that you want to migrate. By default, DMS only allows you to migrate SQL logins. You can enable the ability to migrate Windows logins by granting the target SQL Managed Instance AAD read access. This has to be done in the portal by a Global Administrator.
        1. Reconfigure your instance of the DMS to enable Windows login migrations (user/group).
            1. This option is set up on the portal configuration page. Enabling this setting requires you to restart the DMS to see your change take effect.
        1. After restarting the DMS, Windows logins are available in the list of logins that can be migrated.
            1. Any Windows logins you migrate, will require you to provide their associated domain name.
            1. NT AUTHORITY and NT SERVICE are not supported.
    1. Select Save.
1. Configure migration settings
    1. On the Configure migration settings screen:
        1. Choose source backup option
            1. "I will provide latest backup files": choose this option when you have full backup files for the DMS to use.
            1. "I will let Azure Database Migration Service create backup files": choose this option when you want the DMS to create a full backup of the source database and use it to migrate the source DB via DB restore.
        1. Network location share
            1. This is the local [SMB](https://docs.microsoft.com/en-us/windows/win32/fileio/microsoft-smb-protocol-and-cifs-protocol-overview) network share that DMS can create the source Db backups on.
                1. The service account running SQL Server service on the source Db is required to have write privileges on this network share.
                1. You will need to provide an FQDN or the IP address of the server in the network share.
                1. '\\servername.domainname.com\backupfolder' or '\\IP address\backupfolder'
        1. Username
            1. This is the windows user account the DMS will impersonate to move files to the storage account listed above.
                1. The Windows user requires the full control privilege on the above network share.
                1. The DMS will impersonate that user credential to upload the backup files to the Azure Storage container for the restore process.
                    1. If the source Db’s are TDE encrypted, the above windows user must be the built-in administrator account and [User Account Control](https://docs.microsoft.com/en-us/windows/security/identity-protection/user-account-control/user-account-control-overview) must be **disabled** for the DMS to upload and delete the certificate files.
        1. Password
            1. Password for the user.
        1. Storage account settings
            1. This is the [SAS](https://docs.microsoft.com/en-us/azure/vs-azure-tools-storage-explorer-blobs#get-the-sas-for-a-blob-container) URI that provides the DMS access to the storage account container that the DMS is placing the .bak files to.
            1. This SAS URI must be for the blob container, not for the storage account.
        1. TDE Settings
            1. Select the subscription that contains the target managed instance from the drop-down menu.
            1. Select the target Managed Instance in the drop-down menu.
                1. If the source Db has Transparent Data Encryption (TDE) enabled, you need to have write privileges on the target SQL Managed Instance.
    1. Select Save.
1. Review the migration summary
    1. On the Migration summary screen, in the Activity name text box, specify a name for the migration activity.
        1. Expand the Validation option section to display the Choose validation option screen, specify whether to validate the  ,migrated database for query correctness, and then select Save.
        1. Review and verify the details associated with the migration project.
    1. Select Run migration
1. Run the migration
    1. The DMS migration activity window appear with the status of the migration listed as pending.
1. Monitor the migration
    1. In the DMS migration activity screen, selecting Refresh will update the display.
        1. You can also expand the databases and logins categories to view the migration status of each of those server objects.
    1. Upon completion, Download your report. This will provide you with a listing of the details for the migration process.
    1. Verify that the target database was migrated correctly.

### Online to SQL Server Managed Instance (IN PREVIEW)

In order to setup and execute an Online DMS migration to an Azure SQL Server Managed Instance, there are a couple of things that you just keep in mind. First, this process is in preview mode. Which means that Microsoft could decide that it isn’t something that they want to continue to offer and remove it without notice. Second, using the DMS for an online migration, requires that you use the premium pricing tier when you create your DMS instance. Those two things are important to consider as you decide to move forward or not.

Other important items of note are as follows:

1. You must create a full database backup and place it in the SMB network share that the DMS will use to migrate your databases.
    1. You will also need to provide all subsequent log backups.
    1. The DMS does not run any backups. It uses backups that you provide for the migration.
    1. Be sure that you take backups using the WITH CHECKSUM option.
    1. Do not to append multiple backups (i.e. full and t-log) into a single backup file. Each backup should be on a separate backup file.
    1. Use compressed backups, when possible, to try and cut down on any issues with large backup files.
1. Create your instance of the DMS in the same region as the target database. This will help you avoid any bottlenecks associated with moving large data files across multiple regions.
1. Make sure to schedule your online DMS migration at a time when there is no planned maintenance. Should that type of activity interrupt the process, it will have to be completely restarted.
1. As I mentioned above, pay attention to the [prerequisites](https://docs.microsoft.com/en-us/azure/dms/tutorial-sql-server-managed-instance-online#prerequisites) for this type of migration. It provides you the ability to migrate with less down time, but that requires more due diligence and set up. There is always a tradeoff.

### Differences between Online and Offline migration to Azure SQL Server Managed Instance

As with Azure SQL Database, the DMS online migration isn't <i>that</i> different than the offline. The setup is the same, except for choosing the "Online data migration" option as the activity type for your new migration project. However, that changes when you specify your target details.

#### Difference 1

1. Specify Target Details
    1. On the DMS Migration target details screen, use the Application ID and Key that we set up in the creation of the hybrid DMS instance. The DMS can use that App ID and key to connect to both the target instance of SQL Managed Instance. As well as the Azure Storage Account.
        1. Two other resources that may help you understand how this works are this [link](https://github.optum.com/Dojo360/azure-active-directory-service-principal) to a TerraForm module in the DoJo that creates an Azure Active Directory Service Principal, as well as the Microsoft article [Use portal to create an Azure Active Directory application and service principal that can access resources](https://docs.microsoft.com/en-us/azure/active-directory/develop/howto-create-service-principal-portal).
    1. Choose your Subscription. It should contain the target SQL Managed Instance.
        1. Then, select the target instance. If that isn’t done, or you don’t know how to do that, this [link](https://docs.microsoft.com/en-us/azure/sql-database/sql-database-managed-instance-get-started) from Microsoft might get you pointed in the right direction. However, remember to try and stick to the [Optum playbooks](/docs/azure/managed-sql) as closely as possible. When the SQL Managed Instance has been created, come back to this project to finish the project set up and run the migration.
    1. You will need to provide a SQL User and Password to connect to the SQL Managed Instance.

#### Difference 2

1. Configure migration settings
    1. On the Configure migration settings screen:
    1. Backup settings
        1. Network location share
        1. This is the local [SMB](https://docs.microsoft.com/en-us/windows/win32/fileio/microsoft-smb-protocol-and-cifs-protocol-overview) network share that DMS can create the source Db backups on.
        1. The service account running SQL Server service on the source Db is required to have write privileges on this network share.
        1. You will need to provide an FQDN or the IP address of the server in the network share.
            1. '\\servername.domainname.com\backupfolder' or '\\IP address\backupfolder'.
            1. It is recommended that a separate folder be created for each Db and its corresponding backup file. Those paths can be configured by using the advanced settings option at the bottom of the screen.
        2. Username
            1. This is the windows user account the DMS will impersonate to move files to the storage account listed above.
            1. The Windows user requires the full control privilege on the above network share.
            1. The DMS will impersonate that user credential to upload the backup files to the Azure Storage container for the restore process.
            1. If using Azure File share, use the storage account name pre-pended with AZURE\ as the username.
        1. Password
            1. Password for the user.
            1. In the case that we are using an Azure File Share, the password will be a storage account key.
    1. Storage account settings
        1. Subscription of the Azure storage account
            1. Select the appropriate subscription in the drop down.
        1. Azure Storage Account
            1. This is the Azure storage account container that the DMS uploads the backup files to.
            1. It is recommended that the storage account container be in the same region as the DMS service and the target Db. This will assist with cutting down on slowness in the file upload process.
            1. If the DMS gives you a "System Error 53" or "System Error 57", it probably couldn't connect to the file share. Access will need to be granted from the virtual network. Microsoft has graciously provided [instructions](https://docs.microsoft.com/en-us/azure/storage/common/storage-network-security?toc=/azure/virtual-network/toc.json#grant-access-from-a-virtual-network)for that.

#### Difference 3

1. Performing the migration cutover
    1. After the full database backup is restored on the target instance of SQL Managed Instance, the database is available for performing a migration cutover.
        1. When you're ready to complete the online database migration, select Start Cutover.
        1. Stop all the incoming traffic to source databases.
        1. Take the [tail-log backup], make the backup file available in the SMB network share, and then wait until this final transaction log backup is restored. 
            1. At that point, you'll see Pending changes set to 0.
        1. Select Confirm, and then select Apply.
            1. Something that is worth noting - Microsoft reminds us that the availability (after cutover) of a Business-Critical tier SQL Managed Instance can take a great deal longer than a General Purpose tier SQL Managed Instance. This is because the Business-Critical tier provides three secondary replicas for its Always On High Availability group functionality. Those replicas must be seeded and the time that takes, depends on the size of data. For more information on that process, typical durations, and process check out [Management operations duration](https://docs.microsoft.com/en-us/azure/azure-sql/managed-instance/management-operations-overview#duration).
    1. When the database migration status shows Completed, connect your applications to the new target instance of SQL Managed Instance.

## Tutorials for other Scenarios

1. Migrate RDS SQL Server
    1. [To Azure SQL Database or SQL Managed Instance](https://docs.microsoft.com/en-us/azure/dms/tutorial-rds-sql-server-azure-sql-and-managed-instance-online)
1. Migrate MySQL
    1. [To Azure Database for MySQL](https://docs.microsoft.com/en-us/azure/dms/tutorial-mysql-azure-mysql-online)
1. Migrate RDS MySQL
    1. [To Azure Database for MySQL](https://docs.microsoft.com/en-us/azure/dms/tutorial-rds-mysql-server-azure-db-for-mysql-online)
1. Migrate PostgresSQL
    1. [To Azure DB for PostgreSQL (Portal)](https://docs.microsoft.com/en-us/azure/dms/tutorial-postgresql-azure-postgresql-online-portal)
    1. [To Azure DB for PostgreSQL (Az CLI)](https://docs.microsoft.com/en-us/azure/dms/tutorial-postgresql-azure-postgresql-online)
1. Migrate Azure DB for PostgresSQL - Single Server
    1. [To Azure DB for PostgreSQL (Portal)](https://docs.microsoft.com/en-us/azure/dms/tutorial-azure-postgresql-to-azure-postgresql-online-portal)
1. Migrate RDS PostgresSQL
    1. [To Azure DB for PostgreSQL (Portal)](https://docs.microsoft.com/en-us/azure/dms/tutorial-rds-postgresql-server-azure-db-for-postgresql-online)
1. Migrate MongoDB
    1. [To Azure Cosmos DB Mongo API (offline)](https://docs.microsoft.com/en-us/azure/dms/tutorial-mongodb-cosmos-db)
    1. [To Azure Cosmos DB Mongo API (online)](https://docs.microsoft.com/en-us/azure/dms/tutorial-mongodb-cosmos-db-online)
1. Migrate Oracle
    1. [To Azure DB for PostgreSQL - Single server online](https://docs.microsoft.com/en-us/azure/dms/tutorial-oracle-azure-postgresql-online)
