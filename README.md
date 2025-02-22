<p align="center">
  <img src="https://github.com/user-attachments/assets/1bd97fe8-3e4f-4cc4-8581-50bd69529cab" width="500">
</p>

<p align="center">
  <em>Define it. Duplicate it. Fill it. Profit. 🚀</em>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/release-v2.3.4-brightgreen">
  <img src="https://img.shields.io/badge/scala_version-v2.11.5-orange">
  <img src="https://github.com/ZERDICORP/ez-xlsx/actions/workflows/scala.yml/badge.svg">
  <img src="https://img.shields.io/github/last-commit/ZERDICORP/ez-xlsx">
</p>

#

***Ez-Xlsx is a lightweight Scala library for generating Excel files, built on top of Apache POI. It provides a clean and expressive API to simplify `.xlsx` file creation.***

## 🔧 Installation
Add the following lines to your `build.sbt`:  
```sbt
resolvers += "Reposilite".at("https://repo.nanikin.ru/releases")
libraryDependencies += "com.nanikin" %% "ez-xlsx-apache-poi" % "2.3.4"
```

> 🟢 All usage examples can be found [**here**](https://github.com/ZERDICORP/ez-xlsx/tree/master/examples/apache-poi/src/main/scala).

## 🎯 Philosophy  
✅ **Declarative approach** – *You define what the table should look like, and the library handles the rest.*  
✅ **Separation of table definition and creation** – *Define the table once, and fill it with data any times, ensuring clarity and flexibility.*  
✅ **Clean and lightweight file generation** – *No bulky generation functions; the focus is solely on preparing the data for the file.*

## ✨ Features
- **Simple Table Creation:** Easily create basic tables with rows and columns.
- **Nested Rows Support:** Organize data with nested rows, allowing more complex structures within your tables.
- **Formula Support:** Write and evaluate Excel formulas directly within cells.
- **Cell Styling:** Apply various styles to cells, including font, background, and borders, to make your tables more visually appealing.
- **Multiple Sheets Creation:** Create multiple sheets within a single `.xlsx` file with ease.
- **Performance Optimized:** Fast and efficient, with minimal dependencies for improved performance.

> Ez-Xlsx is designed **only for writing** Excel files and does not support reading `.xlsx` files.

## 🤝 Contributing
We welcome contributions! If you’d like to contribute, feel free to open an [**issue**](https://github.com/ZERDICORP/ez-xlsx/issues) or submit a [**pull request**](https://github.com/ZERDICORP/ez-xlsx/pulls).

There’s always room for improvement! Some ideas for enhancement include:
- *Expanding cell styling options.*
- *Adding more advanced settings for rows and sheets configuration.*
- *Adding more advanced features.*

Your contributions are appreciated!

## 🌌 Preview
<img src="https://github.com/user-attachments/assets/45e640bc-4218-4472-8e5d-d8cd3f1eaf7e" width="700">
