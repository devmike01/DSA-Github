/* 
Copyright (c) 2025 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Owner (

	@SerializedName("login") val login : String,
	@SerializedName("id") val id : Int,
	@SerializedName("node_id") val node_id : String,
	@SerializedName("avatar_url") val avatar_url : String,
	@SerializedName("gravatar_id") val gravatar_id : String,
	@SerializedName("url") val url : String,
	@SerializedName("html_url") val html_url : String,
	@SerializedName("followers_url") val followers_url : String,
	@SerializedName("following_url") val following_url : String,
	@SerializedName("gists_url") val gists_url : String,
	@SerializedName("starred_url") val starred_url : String,
	@SerializedName("subscriptions_url") val subscriptions_url : String,
	@SerializedName("organizations_url") val organizations_url : String,
	@SerializedName("repos_url") val repos_url : String,
	@SerializedName("events_url") val events_url : String,
	@SerializedName("received_events_url") val received_events_url : String,
	@SerializedName("type") val type : String,
	@SerializedName("user_view_type") val user_view_type : String,
	@SerializedName("site_admin") val site_admin : Boolean
)