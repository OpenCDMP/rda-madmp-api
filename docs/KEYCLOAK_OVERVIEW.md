# **Setting Up Keycloak**

This guide provides step-by-step instructions to set up [Keycloak](https://www.keycloak.org/) for **madmp**. Keycloak is used for authentication and authorization within the rda-madmp platform.

#### **Prerequisites**

- Keycloak installed and running.
- Administrative access to Keycloak Admin Console.

---

## Step 1: Create a New Realm

1. **Log In** to the Keycloak Admin Console at `http://localhost:8080/auth` (replace `localhost` with your Keycloak server's address).

2. Click on the **Master** realm dropdown on the left sidebar.

3. Click on **Add Realm**.

4. **Name** the new realm (e.g., `madmp`) and click **Create**.

---

## Step 2: Create OpenID Connect Clients

Create the following five OpenID Connect clients:

- `api`
- `swagger`

:::note
Client names and ids are configurable and are used as examples.
:::

### General Client Settings

For **`api` client**, set the following options:

- **Client Authentication**: **On**
- **Direct Access Grants Enabled**: **On**
- **Service Accounts Enabled**: **On**
- **Standard Flow Enabled**: **Off**

For **`swagger` client**, set the following options:

- **Client Authentication**: **Off**
- **Direct Access Grants Enabled**: **On**
- **Service Accounts Enabled**: **Off**
- **Standard Flow Enabled**: **On**
- **Implicit Flow Enabled**: **On**


### Client: `api`

1. Navigate to **Clients** in the left menu and click **Create**.

2. **Client ID**: `api`

3. **Client Protocol**: **openid-connect**

4. **Root URL**: *(leave blank)*

5. Click **Save**.

6. In the **Settings** tab, ensure the general client settings are applied as specified.

7. Click **Save**.

8. Go to the **Roles** tab and create the following roles:

    - **Admin**
    - **User**
    - **InstallationAdmin**


### Client: `swagger`

1. Create a new client with **Client ID**: `swagger`.

2. **Client Protocol**: **openid-connect**

3. **Root URL**: `https://(APP_URL)/home`

4. Click **Save**.

5. In the **Settings** tab, set:

    - **Client Authentication**: **Off**
    - **Direct Access Grants Enabled**: **On**
    - **Service Accounts Enabled**: **Off**
    - **Standard Flow Enabled**: **On**
    - **Implicit Flow Enabled**: **On**
    - **Valid Redirect URIs**: `https://(APP_URL)/api/swagger-ui/oauth2-redirect.html`
    - **Web Origins**: `/*`

6. Click **Save**.

---

## Step 3: Create Realm Roles

1. In the left menu, click on **Realm Roles**.

2. Click **Add Role**.

3. Create the following roles:

    - **Role Name**: `Admin`
        - Click **Save**.
    - **Role Name**: `User`
        - Click **Save**.

---

## Step 4: Create Client Scopes

Create the following client scopes:

- `api`
- `identity_provider`
- `tenant_role`

For each client scope:

1. Go to **Client Scopes** in the left menu.

2. Click **Create**.

3. **Name**: *(one of the client scope names above)*

4. **Description**: *(optional)*

5. **Type**:
    - all client scopes except `tenant_role`: **None**
    - `tenant_role`: **Optional**

6. **Protocol**: **openid-connect**

7. Click **Save**.

8. For `identity_provider` and `tenant_role` client scopes:

    - **Include in Token Scope**: **On**

---

## Step 5: Configure Mappers for Client Scopes

### Audience Mappers for `api`

For client scope `api`:

1. Go to **Client Scopes** and select the client scope.

2. Navigate to the **Mappers** tab.

3. Click **Create**.

4. **Name**: `Audience`

5. **Mapper Type**: **Audience**

6. **Included Client Audience**: *(select the client scope name, e.g., `api`)*

7. **Add to access token**: **On**

8. **Add to token introspection**: **On**

9. Click **Save**.

### Assign Roles to Client Scopes

For the `api`:

1. Go to the **Scope** tab of each client scope.

2. In **Available Roles**, select the **Realm Roles**:

    - **Admin**
    - **User**

3. Click **Add Selected** to move them to **Assigned Roles**.

### User Session Note Mapper for `identity_provider`

1. Go to **Client Scopes** and select `identity_provider`.

2. Navigate to the **Mappers** tab.

3. Click **Create**.

4. **Name**: `identity_provider`

5. **Mapper Type**: **User Session Note**

6. **User Session Note**: `identity_provider`

7. **Token Claim Name**: `identity_provider`

8. **Claim JSON Type**: **String**

9. **Add to ID token**: **On**

10. **Add to Access Token**: **On**

11. **Add to userinfo**: **On**

12. **Add to access token response**: **On**

13. **Add to token introspection**: **On**

14. Click **Save**.

### User Attribute Mapper for `tenant_role`

1. Go to **Client Scopes** and select `tenant_role`.

2. Navigate to the **Mappers** tab.

3. Click **Create**.

4. **Name**: `tenant_role`

5. **Mapper Type**: **User Attribute**

6. **User Attribute**: `tenant_role`

7. **Token Claim Name**: `tenant_roles`

8. **Claim JSON Type**: `String`

9. **Add to ID token**: **On**

10. **Add to Access Token**: **On**

11. **Add to userinfo**: **On**

12. **Add to token introspection**: **On**

13. **Multivalued**: **On**

14. **Aggregate Attribute Values**: **On**

15. Click **Save**.

---

## Step 6: Assign Client Scopes to Clients

### Client: `api`

1. Go to **Clients** and select `api`.

2. Navigate to the **Client Scopes** tab.

3. In **Optional Client Scopes**, add:

    - `plugins`

### Client: `swagger`

1. Go to **Clients** and select `swagger`.

2. Navigate to the **Client Scopes** tab.

3. In **Default Client Scopes**, add:

    - `api`
---

## Step 7: Set Up Groups

Groups represent roles within the application. Assigning users to groups grants them specific access rights.

### Create Root Group: `madmp`

1. Navigate to **Groups** in the left menu.

2. Click **New**.

3. **Name**: `madmp`

4. Click **Save**.

### Create Subgroups

Under the `madmp` group, create the following subgroups:

- `role-admin`
- `role-installation-admin`
- `role-user`
- `tenant-config-manager`
- `tenant-plan-manager`
- `tenant-role-admin`
- `tenant-role-user`

#### Steps to Create a Subgroup

1. Select the `madmp` group.

2. Go to the **Sub Groups** tab.

3. Click **New**.

4. **Name**: *(e.g., `role-admin`)*

5. Click **Save**.

### Assign Roles to Groups

#### Group: `role-admin`

1. Select `role-admin` under `madmp`.

2. Navigate to **Role Mappings**.

3. In **Client Roles**, select client `api`:

    - Add **Admin**

#### Group: `role-installation-admin`

1. Select `role-installation-admin` under `madmp`.

2. Navigate to **Role Mappings**.

3. In **Client Roles**, select client `api`:

    - Add **InstallationAdmin**
    - Add **User**

#### Group: `role-user`

1. Select `role-user` under `madmp`.

2. Navigate to **Role Mappings**.

3. In **Client Roles**, select client `api`:

    - Add **User**

### Create Subgroups for Tenant Roles

For each tenant role group (`tenant-config-manager`, `tenant-plan-manager`, `tenant-role-admin`, `tenant-role-user`):

1. Select the tenant role group under `madmp`.

2. Go to the **Sub Groups** tab.

3. Click **New**.

4. **Name**: `tenant-default`

5. Click **Save**.

6. Select the `tenant-default` subgroup.

7. Navigate to the **Attributes** tab.

8. Click **Add Attribute**.

9. **Key**: `tenant_role`

10. **Value**: *(corresponding value)*

    - For `tenant-config-manager`: `TenantConfigManager:default`
    - For `tenant-plan-manager`: `TenantPlanManager:default`
    - For `tenant-role-admin`: `TenantAdmin:default`
    - For `tenant-role-user`: `TenantUser:default`

11. Click **Save**.

---

## Step 8: Set Default Groups for Users

All users should by default be members of:

- `/madmp/role-user`
- `/madmp/tenant-role-user/tenant-default`

### Assign Default Groups

1. Navigate to **Realm Settings**.

2. Go to the **Default Groups** tab.

3. Click **Add Groups**.

4. Select:

    - `/madmp/role-user`
    - `/madmp/tenant-role-user/tenant-default`

5. Click **Add Selected**.

---

## Step 9: Create Users

### Create an Admin User

1. Navigate to **Users**.

2. Click **Add User**.

3. **Username**: *(e.g., `admin`)*

4. **Email Verified**: **On**

5. Click **Save**.

6. Go to the **Credentials** tab.

    - Set a password.
    - Disable **Temporary Password**.

7. Go to the **Groups** tab.

    - Click **Join Groups**.
    - Add `/madmp/role-admin`.

### Create a Regular User

1. Repeat steps to create a new user.

2. Add user to `/madmp/role-user` and `/madmp/tenant-role-user/tenant-default` groups.

---

## Step 10: Create `keycloak-api` User

This user allows the application to access Keycloak's REST API.

1. Navigate to **Users**.

2. Click **Add User**.

3. **Username**: `keycloak-api`

4. **Email Verified**: **On**

5. Click **Save**.

6. Go to the **Credentials** tab.

    - Set a password.
    - Disable **Temporary Password**.

7. Go to the **Role Mappings** tab.

    - In **Client Roles**, select `realm-management`.
    - Add **realm-admin**.

8. Go to the **Groups** tab.

    - Add to:

        - `/madmp/role-user`
        - `/madmp/tenant-role-user/tenant-default`

---

## Step 11: Configure Realm Settings

### Enable Email Verification

1. Navigate to **Realm Settings**.

2. In the **Login** tab, enable:

    - **Email Verified**: **On**

### Configure Email Settings

1. In **Realm Settings**, go to the **Email** tab.

2. Set up SMTP settings for sending emails.

    - **Host**
    - **Port**
    - **From**
    - **Encryption**
    - **Authentication**

3. Click **Save**.

### Enable User Registration

1. In **Realm Settings**, go to the **Login** tab.

2. Enable **User Registration**: **On**

3. Under **Default Roles**, add:

    - `/madmp/role-user`
    - `/madmp/tenant-role-user/tenant-default`

---

## Step 12: Configure Identity Providers (Optional)

1. Navigate to **Identity Providers** in the left menu.

2. Click **Add Provider**.

3. Select the provider you wish to integrate (e.g., **Google**, **Facebook**).

4. Enter the required credentials and settings.

5. Click **Save**.

---

## Conclusion

Your Keycloak setup for rda-madmp is now complete. This configuration enables secure authentication and authorization across the various services and components of the platform.

- **Important**: Replace `(APP_URL)` with your actual application URL throughout the configuration.
- **Note**: Ensure that all settings are double-checked for accuracy.

---

## References

- **Keycloak Documentation**: [https://www.keycloak.org/documentation](https://www.keycloak.org/documentation)
- **OpenID Connect Clients**: [Keycloak Clients](https://www.keycloak.org/docs/latest/server_admin/#oidc-clients)
- **Client Scopes**: [Keycloak Client Scopes](https://www.keycloak.org/docs/latest/server_admin/#client-scopes)

## See Also

For complete documentation on configuration, integration, and usage:

- **Configuration Overview**: [Setting Up the Configuration](CONFIGURATION_OVERVIEW.md)
