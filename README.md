# neoburp

NeoBurp is a Burp Suite extension that enhances the HTTP request editor by adding autocomplete for HTTP headers. This extension helps security testers and penetration testers improve their workflow by reducing manual input and ensuring correct header keys and values if it has a default value.

# Features

- Autocompletes HTTP headers in the request editor.
- Reduces typing effort and prevents common typos.
- Ensures correct header keys and default values when applicable.
- Improves efficiency when crafting HTTP requests.
- Seamlessly integrates with Burp Suite.

# Installation
## Prerequisites
- Burp Suite (Community or Professional).
- Java maven.

## Steps
1. Download neoburp-1.0.jar from releases page.
2. Open Burp Suite and navigate to Extensions tab > Installed.
3. Click on `Add` button and select a .jar file.
4. Press `Next` now it's installed.

# Build from source code
1. Clone repository.
```bash
git clone https://github.com/a7med-tal3at/neoburp.git
```
2. Goto neoburp directory.
3. Download all dependencies.
```bash
mvn dependency:resolve
```
4. Once you have all dependencies, you can build the project.
```bash
mvn package
```
5. Output file.
``` 
target/neoburp-1.0-SNAPSHOT.jar
```
6. Now, you can include the jar file in Brup.

# Demo

https://github.com/user-attachments/assets/ea3bdc87-f509-44ce-ae46-4c10b037f657
