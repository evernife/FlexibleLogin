# FlexibleLogin

> ## **Security Notice:** If you use a version 0.16.X version, update immediately to 0.16.5+ or disable the change
password command. 0.16.5 fixed this security bug occurred in this
[commit](https://github.com/games647/FlexibleLogin/commit/43f74a466e73b0f2cfa522b5bfd68480010a7934). Older versions of
0.16.X are not affected.

### Description

A Sponge minecraft server plugin for second authentication. It has a built-in
TOTP support.

Do you want to let your players protect their account (from hackers/login stealers) and keep playing 
while the session server is down. You can use this little plugin. You can protect your account with 
a password you choose or with a time based password created from a secret key, generated just for you.

### Requirements

* Sponge 6.1 or 7.0

### Commands

    /register <password> <password> - Registers using a specific password
    /register - Generates your secret code for TOTP
    /changepassword <password> <password> - Changes your current password
    /login <password/code> - Login using your password or time based code
    /logout - Logs you out
    /setemail - Sets your email address
    /forgotpassword - Sends a recovery email to the email address
    /unregister <uuid/name> - delete an account

    Admin commands
    /flexiblelogin reload - reloads the config
    /flexiblelogin unregister <name/uuid> - Deletes the account of a user
    /flexiblelogin register <name/uuid> <pass> - Register the user with a specific password
    /flexiblelogin resetpw <name> - Sets a new temp password for a new user

### Permissions

    flexiblelogin.admin - Permission to delete accocunts
    flexiblelogin.command.login - Use the /login command
    flexiblelogin.command.logout - Use the /logout command
    flexiblelogin.command.changepw - Use the /changepassword command
    flexiblelogin.command.register - Use the /register command
    flexiblelogin.command.email - Use the /setemail command
    flexiblelogin.command.forgot - Use the /forgot command
    flexiblelogin.no_auto_login - Players who have this permissions won't be auto logged in by the ip auto login feature
    flexiblelogin.bypass - Users who have this permission can skip authentication

### Config

    # Do you allow your users to skip authentication with the bypass permission
    bypassPermission=false
    # Should only the specified commands be protected from unauthorized access
    commandOnlyProtection=false
    # Email configuration for password recovery
    emailConfiguration {
        # Username for the account you want to the email from
        account=""
        # Is password recovery using an email alloweds
        enabled=false
        # Mail server
        host="smtp.gmail.com"
        # Password for the account you want to the email from
        password=""
        # SMTP Port for outgoing messages
        port=465
        # Displays as sender in the email client
        senderName="Your minecraft server name"
        # Email subject/title
        subject="Your new Password"
        # Email contents. You can use HTML here
        text="New password for %player% on Minecraft server %server%: %password%"
    }
    # Algorithms for hashing user passwords. You can also choose totp
    hashAlgo=bcrypt
    # Should the plugin login users automatically if it's the same account from the same IP
    ipAutoLogin=false
    # Custom command that should run after the user tried to make too many attempts
    lockCommand=""
    # How many login attempts are allowed until everything is blocked
    maxAttempts=3
    # How many accounts are allowed per ip-addres. Use 0 to disable it
    maxIpReg=0
    # The user should use a strong password
    minPasswordLength=4
    # Should this plugin check for player permissions
    playerPermissions=false
    # If command only protection is enabled, these commands are protected. If the list is empty 
    # all commands are protected
    protectedCommands=[
        op,
        pex
    ]
    # Database configuration
    sqlConfiguration {
        # Database name
        database=flexiblelogin
        # Password in order to login
        password=""
        # Path where the database is located. This can be a file path (h2/SQLite) or an IP/Domain(MySQL/MariaDB)
        path="%DIR%"
        # Port for example MySQL connections
        port=3306
        # SQL server type. You can choose between h2, SQLite and MySQL
        type=H2
        # Username to login the database system
        username=""
    }
    teleportConfig {
        coordX=0
        coordY=0
        coordZ=0
        # Should the plugin use the default spawn from the world you specify below
        defaultSpawn=false
        enabled=false
        # Spawn world or let it empty to use the default world specified in the server properties
        worldName=""
    }
    # Number of seconds a player has time to login or will be kicked.-1 deactivates this features
    timeoutLogin=60
    # Should the plugin save the login status to the database
    updateLoginStatus=false
    # How seconds the user should wait after the user tried to make too many attempts
    waitTime=300

### Download

https://github.com/games647/FlexibleLogin/releases

### Screenshots:

#### TOTP Key generation (/register)
![Minecraft image picture](https://i.imgur.com/K2GDqfW.png)

#### Android App Google Authenticator (IOS App exists too)
![Google authenticator](https://i.imgur.com/Zz6RkdY.png)
You can see there a time generated code which can be used for the login process. `/login <code>`
Additionally it display your user account name and the server ip.
