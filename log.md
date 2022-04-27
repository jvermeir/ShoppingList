# Log book

This file is a history of the experiments I've done and what I learned along the way.

## 20220427

Copying code from the NX tutorial, chapter 4 (https://nx.dev/react-tutorial/04-connect-to-api). 
I've used a menu item with a name instead of todos. The tutorial code goes into `apps/shop/src/app/app.tsx`.
Instead of a node service, I'll use a Kotlin/SpringBoot service. To create a skeleton I've used a NX plugin called nx rocks (https://www.npmjs.com/package/@nxrocks/nx-spring-boot). 

```
npm install @nxrocks/nx-spring-boot --save-dev
nx g @nxrocks/nx-spring-boot:new shop-api
```

Answer the questions like this:

```
nx g @nxrocks/nx-spring-boot:new shop-api
✔ What kind of project are you generating? · application
✔ Which build system would you like to use? · gradle-project
✔ Which packaging would you like to use? · jar
✔ Which version of Java would you like to use? · 11
✔ Which language would you like to use? · kotlin
✔ What groupId would you like to use? · nl.vermeir
✔ What artifactId would you like to use? · shop-api
✔ What package name would you like to use? · nl.vermeir.shop
✔ What is the project about? · shop till you drop
✔ What dependencies would you like to use (comma separated)?
```

This creates a folder named `shop/apps/shop-api` and adds a line to `shop/workspace.json`: 

```
"shop-api": "apps/shop-api"
```

I'll replace the files in shop-api by a starter app generated on the spring boot website, except `project.json`. This file defines the build targets 
needed for the Kotlin service, so I'll keep it. 
To generate a shop-api Kotlin service, I've used this tutorial https://kotlinlang.org/docs/jvm-spring-boot-restful.html. This first creates a starter application and a zip file. 
I've downloaded the zip file and used it to replace the contents of `shop/apps/shop-api`, but I've KEPT THE PROJECT.JSON FILE as it was generated earlier.
The kotlin/spring-boot tutorial shows how to create web services and a database. Note that the table name in 

```
@Table("MENUITEMS")
data class MenuItem(@Id val id: String?, val name: String)
```

Back to the nx tutorial here: https://nx.dev/react-tutorial/06-proxy. Since I didn't complete the step before (where a node service was created), I've just created `shop/proxy.conf.json` like this:

```
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false
  }
}
```

needs to be uppercase. 

## 20220426

### Reboot of the shopping list project using NX

I've decided to build this new release centred on Typescript and React, with a Kotlin service to store data. Using NX it seems easiest to start with a React/Typescript starter and add a Kotlin service later.

The NX tutorial can be found here: https://nx.dev/react-tutorial/01-create-application

```
$ npx create-nx-workspace@latest

npx: installed 58 in 4.48s
✔ Workspace name (e.g., org name)     · shop
✔ What to create in the new workspace · react
✔ Application name                    · shop
✔ Default stylesheet format           · scss
/Users/jan/.npm/_npx/36286/lib/node_modules/create-nx-workspace/bin
✔ Use Nx Cloud? (It's free and doesn't require registration.) · No

 >  NX   Nx is creating your v14.0.3 workspace.

```

I've chosen react and scss (because I completed a course by Jonas Schmedtmann, see https://www.udemy.com/course/advanced-css-and-sass/. It takes a bit, but I think it was well worth my time and excellent value for money).

Start the app:

```
cd shop
nx serve shop
```

