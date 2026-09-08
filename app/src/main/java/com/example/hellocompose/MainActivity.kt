@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.foto_profile),
            contentDescription = "Foto Profil",
            modifier = Modifier.size(120.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Farrell Fyelo Hollans Tamaela", style = MaterialTheme.typography.headlineLarge)
        Text(text = "NIM: 22001", style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ProfilePreview() {
    HelloComposeTheme {
        Surface { ProfileScreen() }
    }
}