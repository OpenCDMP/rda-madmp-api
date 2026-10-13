# DmpRootElasticEntity — Model

## Overview
The **`DmpRootElasticEntity`** class represents the **root Elasticsearch entity** for a Data Management Plan (DMP) within the `rda-madmp-api`.  
It defines the top-level structure of how DMPs are indexed, stored, and queried in Elasticsearch.

This entity encapsulates:
- The DMP metadata and content (`DmpElasticEntity`)
- Associated users (`DmpUserElasticEntity`)
- Access and status information
- Tenant context for organizations or projects

---


## Fields

| Field            | Type                         | Description                                                                                                                                                                  |
|------------------|------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **`id`**         | `String`                     | Unique identifier for the DMP root entity in Elasticsearch. Used as the primary key for indexing and retrieval.                                                              |
| **`dmp`**        | `DmpElasticEntity`           | Represents the actual DMP content and metadata according to the RDA-MaDMP model.                                                                                             |
| **`users`**      | `List<DmpUserElasticEntity>` | A list of user entities associated with the DMP (e.g., creators, contributors, owners). Useful for access control and auditing. (Internal field — not visible to end users.) |
| **`tenantId`**   | `UUID`                       | Identifies the organization, project, or tenant under which the DMP belongs. Supports multi-tenant deployments. *(Internal field — not visible to end users.)*               |
| **`accessType`** | `AccessType`                 | Defines the access level of the DMP (e.g., **public**, **private**). *(Internal field — controls data visibility and indexing behavior.)*                                    |
| **`status`**     | `Status`                     | Indicates the current status of the DMP (e.g., **draft**, **active**). *(Internal field — used for workflow and synchronization logic.)*                                     |
| **`actionType`** | `ActionType`                 | Describes the operation tha **OpenCDMP** will receive.                                                                                                                       |

---

### Internal Fields Notice

The following fields are **system-managed** and **not exposed to end users** through the API:

- `users`
- `tenantId`
- `accessType`
- `status`
- `actionType`

These fields are primarily used for:
- Internal access control and multi-tenant separation.
- Tracking DMP lifecycle and synchronization states.
- Determining how a DMP should be **processed**, **indexed**, or **updated** within the backend system.

---

# DmpElasticEntity — Model 

## Overview
The **`DmpElasticEntity`** class represents the **core DMP (Data Management Plan) document** as defined by the [RDA DMP Common Standard](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard).  
It contains all main descriptive, structural, and metadata fields that make up a DMP, such as the contact person, datasets, projects, costs, and ethical statements.

This entity is embedded within the `DmpRootElasticEntity`.

---

## Top Level Fields

| Field                          | Type                             | Description                                                                                              |
|--------------------------------|----------------------------------|----------------------------------------------------------------------------------------------------------|
| **`alternate_identifier`**     | `Nested Data Structure`           | Alternative or secondary identifiers for a DMP.                          |
| **`contact`**                  | `Nested Data Structure`           | The primary contact person for the DMP, including name, email, and affiliation.                          |
| **`contributor`**              | `Nested Data Structure` | List of individuals or organizations who contributed to the DMP (e.g., data stewards, co-investigators). |
| **`cost`**                     | `Nested Data Structure`        | Details on costs related to data management, storage, or preservation activities.                        |
| **`created`**                  | `Date`                           | Timestamp of when the DMP was originally created.                                                        |
| **`dataset`**                  | `Nested Data Structure`     | Datasets described in the DMP, including metadata, accessibility, and preservation details.              |
| **`description`**              | `String`                         | General description of the DMP, outlining its scope and objectives.                                      |
| **`dmpId`**                    | `Nested Data Structure`             | Globally unique identifier for the DMP (e.g., DOI or internal handle).                                   |
| **`ethicalIssuesDescription`** | `String`                         | Description of ethical considerations or issues relevant to the DMP.                                     |
| **`ethicalIssuesExist`**       | `Term from Controlled Vocabulary`         | Indicates whether ethical issues are present (`yes`, `no`, or `unknown`).                                |
| **`ethicalIssuesReport`**      | `String`                         | A link or reference to an external ethical issues report (string representation of a URI).               |
| **`language`**                 | `Term from Controlled Vocabulary`                   | Specifies the language of the DMP (e.g., `en`, `fr`, `de`).                                              |
| **`modified`**                 | `Date`                           | Timestamp of the most recent modification to the DMP.                                                    |
| **`project`**                  | `Nested Data Structure`     | Projects associated with the DMP, including title, start/end dates, and funding details.                 |
| **`related_identifier`**       | `String`                         | Identifiers of related resources, e.g. a project page, previous DMP versions,             |
| **`title`**                    | `String`                         | Title of the DMP, typically corresponding to the project or research activity it describes.              |

---

## See Also

For complete documentation on configuration, integration, and usage:

- **RDA DMP Common Standard**: https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard
- **API specification**: [`openapi.yaml`](../openapi.yaml)
- **Supported maDMP JSON Schema versions:** [1.3](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard/blob/master/examples/JSON/JSON-schema/1.3/maDMP-schema-1.3.json) and [1.2](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard/blob/master/examples/JSON/JSON-schema/1.2/maDMP-schema-1.2.json)