# GeoHash编码工具
## 简介
*Geohash*是Gustavo Niemeyer于2008年发明的地理编码系统，将地理位置编码为一小段字母和数字。它是一种分层的空间数据结构，实际上是将空间细分为网格状的桶，通过*Z-order空间填充曲线*进行空间网格连结。*Z-order空间填充曲线*又称为*Morton空间填充曲线*或*Morton码*,早在1996年, Morton首次应用于文件排序，后来被用于多维空间降至一维编码。
## 主要实现功能
* *GeoHash* `base4`,`base16`,`base32`编码
* 距离估算
* 最邻近块计算
## 精度参考
|lvl | bits |   error       |    base4   |  base16  |  base64   |
|----| ---- |       ----    |   ----     |  ----    | ----      |
|  0 |   0  |  20015.087 km |   prec  0  |  prec 0  |  prec 0   |
|  1 |   2  |  10007.543 km |   prec  1  |          |           |
|  2 |   4  |   5003.772 km |   prec  2  |  prec 1  |           |
|  3 |   6  |   2501.886 km |   prec  3  |          |  prec 1   |
|  4 |   8  |   1250.943 km |   prec  4  |  prec 2  |           |
|  5 |  10  |    625.471 km |   prec  5  |          |           |
|  6 |  12  |    312.736 km |   prec  6  |  prec 3  |  prec 2   |
|  7 |  14  |    156.368 km |   prec  7  |          |           |
|  8 |  16  |     78.184 km |   prec  8  |  prec 4  |           |
|  9 |  18  |     39.092 km |   prec  9  |          |  prec 3   |
| 10 |  20  |     19.546 km |   prec 10  |  prec 5  |           |
| 11 |  22  |   9772.992  m |   prec 11  |          |           |
| 12 |  24  |   4886.496  m |   prec 12  |  prec  6 |  prec 4   |
| 13 |  26  |   2443.248  m |   prec 13  |          |           |
| 14 |  28  |   1221.624  m |   prec 14  |  prec  7 |           |
| 15 |  30  |    610.812  m |   prec 15  |          |  prec 5   |
| 16 |  32  |    305.406  m |   prec 16  |  prec  8 |           |
| 17 |  34  |    152.703  m |   prec 17  |          |           |
| 18 |  36  |     76.351  m |   prec 18  |  prec  9 |  prec 6   |
| 19 |  38  |     38.176  m |   prec 19  |          |           |
| 20 |  40  |     19.088  m |   prec 20  |  prec 10 |           |
| 21 |  42  |    954.394 cm |   prec 21  |          |  prec 7   |
| 22 |  44  |    477.197 cm |   prec 22  |  prec 11 |           |
| 23 |  46  |    238.598 cm |   prec 23  |          |           |
| 24 |  48  |    119.299 cm |   prec 24  |  prec 12 |  prec 8   |
| 25 |  50  |     59.650 cm |   prec 25  |          |           |
| 26 |  52  |     29.825 cm |   prec 26  |  prec 13 |           |
| 27 |  54  |     14.912 cm |   prec 27  |          |  prec 9   |
| 28 |  56  |      7.456 cm |   prec 28  |  prec 14 |           |
| 29 |  58  |      3.728 cm |   prec 29  |          |           |
| 30 |  60  |      1.864 cm |   prec 30  |  prec 15 |  prec 10  |
| 31 |  62  |      0.932 cm |   prec 31  |          |           |
| 32 |  64  |      0.466 cm |   prec 32  |  prec 16 |           |