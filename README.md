# UIL Extractor

`uil-extractor` is a Java library that turns a multiple-choice contest packet PDF into one
in-memory image per question row (or per group of questions that share a table row). It works on
packets that lay their questions out as a bordered table and find the printed `QUESTION n` labels.

Nothing is written to disk — you get `BufferedImage`s back and decide what to do with them.

## Requirements

- Java 17+
- Apache PDFBox (pulled in automatically as a compile dependency)

## Installation

Once published, add it to your build:

```xml
<dependency>
    <groupId>io.github.7lhscs</groupId>
    <artifactId>uil-extractor</artifactId>
    <version>1.0.0</version>
</dependency>
```

To use a locally-built copy without publishing:

```bash
mvn clean install
```

## Usage

```java
import java.nio.file.Path;
import java.util.List;
import uil.extractor.ExtractedImage;
import uil.extractor.ExtractorOptions;
import uil.extractor.QuestionImageExtractor;

// Default resolution (200 DPI).
List<ExtractedImage> images = new QuestionImageExtractor().extract(Path.of("packet.pdf"));

// Or a custom resolution.
List<ExtractedImage> highRes =
        new QuestionImageExtractor(new ExtractorOptions(300f)).extract(Path.of("packet.pdf"));

for (ExtractedImage image : images) {
    // Which question(s) this image covers, and where it came from.
    System.out.println("questions " + image.region().firstNumber()
            + "-" + image.region().lastNumber()
            + " on page " + (image.region().pageIndex() + 1));

    // The cropped image, held in memory. Save it, display it, or post-process it.
    java.awt.image.BufferedImage pixels = image.image();
}
```

`ExtractedImage` is a record of `(QuestionRegion region, BufferedImage image)`. A region may cover
several question numbers when a group of questions shares a single table row (for example, a shared
code listing) — those come back as one image with a first/last number span.

### Writing PNGs yourself

The library does not touch the file system. If you want files on disk, name and write them in your
own code, for example:

```java
for (ExtractedImage image : images) {
    var region = image.region();
    String name = region.firstNumber() == region.lastNumber()
            ? String.format("q%02d.png", region.firstNumber())
            : String.format("q%02d-%02d.png", region.firstNumber(), region.lastNumber());
    javax.imageio.ImageIO.write(image.image(), "png", outputDir.resolve(name).toFile());
}
```

## Building and testing

```bash
mvn test          # run the unit + integration tests
mvn clean install # build and install into your local ~/.m2 repository
```

## Publishing

Publishing is wired through a `release` profile so that a plain `mvn test` / `mvn install` never
signs or uploads anything. A full signed publish to Maven Central is:

```bash
mvn clean deploy -Prelease
```

Before your first deploy, complete this checklist:

1. **Claim the namespace.** The `groupId` is already set to `io.github.7lhscs`. Sonatype verifies
   GitHub-based namespaces by having you create a **public repository named exactly
   `io.github.7lhscs`** under the `7lhscs` GitHub account (a one-time proof of ownership, which you
   can delete afterward). Start this on <https://central.sonatype.com> → *Namespaces* → *Add
   Namespace*, then link the GitHub account and create the verification repo when prompted.

2. **Fill in the identity metadata.** Replace every `TODO(identity)` placeholder in `pom.xml` —
   `<url>`, `<licenses>`, `<developers>`, and `<scm>`. Central rejects deployments that contain
   placeholder or fabricated values, so these must be real and verifiable.

3. **Register on the Central Portal.** Create an account at <https://central.sonatype.com> and
   generate a **publishing token** (username + password).

4. **Add credentials to `~/.m2/settings.xml`.** The `publishingServerId` in the POM is `central`,
   so the server entry must use that id:

   ```xml
   <settings>
     <servers>
       <server>
         <id>central</id>
         <username>YOUR_PORTAL_TOKEN_USERNAME</username>
         <password>YOUR_PORTAL_TOKEN_PASSWORD</password>
       </server>
     </servers>
   </settings>
   ```

5. **Set up GPG signing.** Central requires every artifact to be signed. Generate a key pair if you
   don't have one, publish your public key to a key server, and make it available to Maven:

   ```bash
   gpg --gen-key                       # create a key pair
   gpg --keyserver keyserver.ubuntu.com --send-keys YOUR_KEY_ID
   ```

   The `maven-gpg-plugin` runs in the `verify` phase. You can test the full release build without
   actually signing or uploading by using `-Dgpg.skip=true`.

6. **Deploy.** Run `mvn clean deploy -Prelease`. The `central-publishing-maven-plugin` uploads the
   bundle, waits for Central to validate it (`waitUntil=validated`), and leaves it pending release
   in the portal. Set `<autoPublish>` to `true` in the POM if you'd rather it release automatically
   once validated.

### What the `release` profile produces

| Artifact | Plugin | Required by Central |
| --- | --- | --- |
| `uil-extractor-<version>.jar` | `maven-jar-plugin` (default) | yes |
| `uil-extractor-<version>-sources.jar` | `maven-source-plugin` | yes |
| `uil-extractor-<version>-javadoc.jar` | `maven-javadoc-plugin` | yes |
| `.asc` signature for each | `maven-gpg-plugin` | yes |

## Documentation

- Package overview: `src/main/java/uil/extractor/package-info.java`
- Architecture and UML diagrams: [`docs/UML.md`](docs/UML.md)
