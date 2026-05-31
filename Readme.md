<h1>Project COZY</h1>
<h3>COllaboration made eaZY</h3>

<h3>Local Profile Image Storage</h3>
<ul>
  <li>Uploaded profile images are stored under <code>uploads/profile_images</code>.</li>
  <li>Spring serves uploaded images from the configured static location <code>file:uploads/</code>.</li>
  <li>Set <code>FILE_STORAGE_UPLOAD_ROOT</code> to change the local upload directory.</li>
  <li><code>DEFAULT_PROFILE_IMAGE_KEY</code> is optional and defaults to <code>profile_images/Default_Profile.png</code>.</li>
</ul>

<h3>Build & Run</h3>
<ol>
  <li>Set environment variables:
    <code>JWT_SECRET</code>,
    <code>POSTGRES_URL</code>,
    <code>POSTGRES_USER</code>,
    <code>POSTGRES_PASSWORD</code>
  </li>
  <li>Build: <code>./gradlew clean bootJar</code></li>
  <li>Run: <code>nohup java -jar build/libs/collaboproject-be-0.0.1-SNAPSHOT.jar &gt; app.log 2&gt;&amp;1 &amp;</code></li>
</ol>
