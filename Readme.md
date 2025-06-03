
#Theming:

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

