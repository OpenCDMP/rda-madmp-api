# Integration Overview for OpenCDMP

The **rda-madmp-api** operates as an independent service for handling and transforming data into **RDA-compliant machine-actionable Data Management Plans (maDMPs)**.  
However, it also supports **integration with the OpenCDMP platform**, enabling seamless exchange and conversion of DMP data between the two systems.

---

## About OpenCDMP

**[OpenCDMP](https://github.com/OpenCDMP/OpenCDMP) is an open and extensible software platform designed to simplify the management, monitoring, and maintenance of Output Management Plans (OMPs), such as Data Management Plans (DMPs) and Software Management Plans (SMPs). It provides a flexible and customizable environment that streamlines the complex processes involved in handling OMPs, ensuring efficiency, compliance, and collaboration across various projects and organization.**

OpenCDMP enables:
- Centralized management of DMPs across projects and organizations.
- Role-based collaboration between multiple contributors.
- Integration with external services (e.g., repositories, PID providers, maDMP converters).
- Automated export of DMPs in multiple formats (PDF, JSON, XML).

The **rda-madmp-api** extends this ecosystem by serving as the **conversion and interoperability layer**, transforming OpenCDMP’s internal data model into the **RDA machine-actionable DMP (maDMP)** format for reuse, publication, and indexing.

For more details about the platform, visit the official [OpenCDMP documentation](https://openCDMP.github.io/).

---

## Message Exchange via RabbitMQ

Communication between ΟpenCDMP and the `rda-madmp-api` is handled asynchronously via **RabbitMQ**, using **QueueInbox** and **QueueOutbox** mechanisms.

- **QueueInbox** – receives messages from ΟpenCDMP and stores them in **PostgreSQL**.
- **QueueOutbox** – publishes processed or transformed messages back to ΟpenCDMP or other connected systems.
- **Elasticsearch** – used **only for indexing and search**, not for message passing or queueing.

---

## Data Model Accepted from ΟpenCDMP

The `rda-madmp-api` accepts the following data structure from ΟpenCDMP as input, for more detailed look visit [Common Models for ΟpenCDMP](https://github.com/openCDMP/common-models).

| Field                  | Type                                 | Description                                        |
|------------------------|--------------------------------------|----------------------------------------------------|
| `id`                   | `UUID`                               | Unique identifier for the plan.                    |
| `version`              | `short`                              | Version number of the plan.                        |
| `label`                | `String`                             | Title for the plan.                                |
| `description`          | `String`                             | Description of the plan.                           |
| `accessType`           | `PlanAccessType`                     | Defines visibility (e.g., public, private).        |
| `status`               | `PlanStatusModel`                    | Current plan status (e.g., draft, finalized).      |
| `creator`              | `UserModel`                          | User who originally created the plan.              |
| `planBlueprint`        | `PlanBlueprintModel`                 | Template that the plan is based on.                |
| `properties`           | `PlanPropertiesModel`                | Additional configuration of properties.            |
| `entityDois`           | `List<EntityDoiModel>`               | List of DOIs assigned to the plan entities.        |
| `descriptions`         | `List<DescriptionModel>`             | The Descriptions that are within the plan.         |
| `users`                | `List<PlanUserModel>`                | Users associated with the plan and their roles.    |
| `references`           | `List<PlanReferenceModel>`           | Linked external references or identifiers.         |
| `descriptionTemplates` | `List<PlanDescriptionTemplateModel>` | Template that the descriptions are based on.       |
| `pdfFile`              | `FileEnvelopeModel`                  | PDF export of the plan.                            |
| `rdaJsonFile`          | `FileEnvelopeModel`                  | RDA-compliant JSON export of the plan.             |
| `supportingFilesZip`   | `FileEnvelopeModel`                  | ZIP file containing additional attachments.        |
| `previousDOI`          | `String`                             | DOI from the previous version of the plan, if any. |
| `finalizedAt`          | `Instant`                            | Timestamp when the plan was finalized.             |
| `publicAfter`          | `Instant`                            | Date after which the plan becomes public.          |
| `updatedAt`            | `Instant`                            | Last modification timestamp.                       |
| `createdAt`            | `Instant`                            | Creation timestamp.                                |
| `language`             | `String`                             | Language code (e.g., "en", "de").                  |

---

## Conversion Process

1. **Message Sent**  
   ΟpenCDMP sends the DMP payload to RabbitMQ.

2. **Message Consumed**  
   The `rda-madmp-api` consumes the message and validates its structure.

3. **Transformation**  
   The service maps the incoming model into the **RDA DMP Common Standard** format.

4. **Persistence**  
   The transformed DMP is stored in **Elasticsearch**.

---

## See Also

For complete documentation on configuration, integration, and usage:

- **OpenCDMP Documentation Overview**: https://opencdmp.github.io/
- **Common Models**: [Documentation](https://opencdmp.github.io/developers/plugins/common-models/) & [Common Models for ΟpenCDMP](https://github.com/openCDMP/common-models).
- **Supported maDMP JSON Schema versions:** [1.3](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard/blob/master/examples/JSON/JSON-schema/1.3/maDMP-schema-1.3.json) and [1.2](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard/blob/master/examples/JSON/JSON-schema/1.2/maDMP-schema-1.2.json)
