package com.eltonkola.model

import java.time.LocalDate

val fakePosts = listOf(

    BlogPost(
        metadata = PostMetadata(
            title = "First post",
            tags = listOf("tag1", "tag2", "tag3"),
            slug = "first-post",
            date = LocalDate.of(2023, 1, 1),
        ),
        content = "This is the content of the first post",
        fileName = "first-post.md",
    ),

    BlogPost(
        metadata = PostMetadata(
            title = "Second post",
            tags = listOf("tag1", "tag2", "tag3"),
            slug = "second-post",
            date = LocalDate.of(2023, 1, 2),
        ),
        content = "This is the content of the second post",
        fileName = "second-post.md",
    ),
    BlogPost(
        metadata = PostMetadata(
            title = "Third post",
            tags = listOf("tag1", "tag2", "tag3"),
            slug = "third-post",
            date = LocalDate.of(2023, 1, 3),
        ),
        content = "This is the content of the third post",
        fileName = "third-post.md",
    ),

    BlogPost(
        metadata = PostMetadata(
            title = "4 post",
            tags = listOf("tag1", "tag2", "tag3"),
            slug = "third-post",
            date = LocalDate.of(2023, 1, 3),
        ),
        content = "This is the content of the third post",
        fileName = "third-post.md",
    ),

    BlogPost(
        metadata = PostMetadata(
            title = "5 post",
            tags = listOf("tag1", "tag2", "tag3"),
            slug = "third-post",
            date = LocalDate.of(2023, 1, 3),
        ),
        content = "This is the content of the third post",
        fileName = "third-post.md",
    ),
)