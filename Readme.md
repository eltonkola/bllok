#Bllok
This is a simple static web generator.

you can use it from a github action:

https://github.com/eltonkola/bllok-action

or manually:
```
java -jar .\bllok-1.0-SNAPSHOT.jar tema blog public
```
here is an example of a blog created and updated entirely from github:
https://github.com/eltonkola/bllok_skenderbeu

## Paranms

### 1 rootPath
yhe theme you want to use
### 2 rootPath
the content folder
### 3 rootPath
the output folder
### 4 rootPath
dont pass this param if the page will be published on the root folder of your domain/subdomain
for this github page: https://eltonkola.github.io/bllok_skenderbeu/ it would be:

```
java -jar .\bllok-1.0-SNAPSHOT.jar tema blog public bllok_skenderbeu
```

More coming soon


##Theme config
copy one of the exiting themes and customize them, or create one from zero, it should be easy.





##Theming:

{{ websiteName }}

{{ for page in pages }}
    <a href="{{ page.link }}">{{ page.name }}</a>
{{ end }}

{{ post.title }}
{{ post.date }}
{{ post.content }}


{{ for category in categories }}
{{ partial categoryItem with category }}
{{ end }}


categoryItem partial:

 <li class="has-children">
    <a href="#" class="category-parent-link">
        {{ this.name }}
    </a> 
    {{ if this.subCategories }}
        <span class="category-toggle">[+]</span>
    {{ endif }}
    <ul>
         {{ for subcategory in this.subCategories }}
            {{ partial categoryItem with subcategory }}
        {{ endfor }}
    </ul>
 </li>


{{ for recentpost in recentposts }}
<a href="{{ recentpost.link }}">{{ recentpost.title }}</a>
{{ endfor }}

{{ websiteName }}
{{ websiteDescription }}
{{ baseUrl }}
{{ feedEmail }}
{{ feedEmailRealName }}
{{ socials }}
{{ language }}
{{ websiteCopyright }}

