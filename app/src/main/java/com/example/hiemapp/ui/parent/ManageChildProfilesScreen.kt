package com.example.hiemapp.ui.parent

import android.graphics.BitmapFactory
import android.graphics.ColorFilter
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.hiemapp.R
import com.example.hiemapp.ui.theme.HiEmAppTheme
import com.example.hiemapp.ui.theme.ReadexPro
import com.example.hiemapp.ui.viewmodel.ManageChildProfilesViewModel
import androidx.compose.ui.graphics.BlendMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageChildProfilesScreen(
    manageChildProfilesViewModel: ManageChildProfilesViewModel = viewModel()
) {
    val childProfiles by manageChildProfilesViewModel.childProfiles.collectAsState()
    var showAddProfileDialog by remember { mutableStateOf(false) }
    var selectedProfile by remember { mutableStateOf<ChildProfile?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Quản lý hồ sơ trẻ",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = ReadexPro
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddProfileDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                icon = { Icon(Icons.Filled.Add, contentDescription = "Thêm") },
                text = {
                    Text("Thêm hồ sơ", fontFamily = ReadexPro)
                },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (childProfiles.isEmpty()) {
                EmptyProfileView(onAddClick = { showAddProfileDialog = true })
            } else {
                ProfileGridSection(
                    profiles = childProfiles,
                    selectedProfile = selectedProfile,
                    onProfileSelected = { profile ->
                        selectedProfile = profile
                    },
                    onDeleteProfile = { profile ->
                        manageChildProfilesViewModel.deleteChildProfile(profile)
                        if (selectedProfile?.id == profile.id) {
                            selectedProfile = null
                        }
                    }
                )
            }

            selectedProfile?.let { profile ->
                ProfileDetailsCard(profile = profile)
            }
        }

        if (showAddProfileDialog) {
            AddChildProfileDialog(
                onDismissRequest = { showAddProfileDialog = false },
                manageChildProfilesViewModel = manageChildProfilesViewModel
            )
        }
    }
}

@Composable
private fun EmptyProfileView(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_child_placeholder),
            contentDescription = "Không có hồ sơ",
            modifier = Modifier
                .size(120.dp)
                .alpha(0.5f), // Giảm độ trong suốt
            colorFilter = null
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Chưa có hồ sơ trẻ nào",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = ReadexPro,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Hãy thêm hồ sơ đầu tiên để bắt đầu",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = ReadexPro,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {
            Text("Thêm hồ sơ mới", fontFamily = ReadexPro)
        }
    }
}

@Composable
private fun ProfileGridSection(
    profiles: List<ChildProfile>,
    selectedProfile: ChildProfile?,
    onProfileSelected: (ChildProfile) -> Unit,
    onDeleteProfile: (ChildProfile) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Danh sách hồ sơ",
            style = MaterialTheme.typography.titleMedium.copy(
                fontFamily = ReadexPro,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(profiles) { profile ->
                ProfileCard(
                    profile = profile,
                    isSelected = selectedProfile?.id == profile.id,
                    onClick = { onProfileSelected(profile) },
                    onDelete = { onDeleteProfile(profile) }
                )
            }
        }
    }
}

@Composable
private fun ProfileCard(
    profile: ChildProfile,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    }

    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                ProfileAvatar(profile = profile)

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Xóa hồ sơ",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = profile.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = ReadexPro,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "${profile.age} tuổi • ${if (profile.gender == Gender.MALE) "Nam" else "Nữ"}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = ReadexPro,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )
        }
    }
}

@Composable
private fun ProfileAvatar(profile: ChildProfile) {
    val painter = if (!profile.avatar.isNullOrEmpty()) {
        rememberAsyncImagePainter(model = profile.avatar)
    } else {
        painterResource(id = R.drawable.hiemlogo)
    }

    Image(
        painter = painter,
        contentDescription = "Ảnh đại diện của ${profile.name}",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
    )
}

@Composable
private fun ProfileDetailsCard(profile: ChildProfile) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Thông tin chi tiết",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = ReadexPro,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            ProfileDetailItem(label = "Tên", value = profile.name)
            ProfileDetailItem(
                label = "Giới tính",
                value = if (profile.gender == Gender.MALE) "Nam" else "Nữ"
            )
            ProfileDetailItem(label = "Tuổi", value = "${profile.age} tuổi")
        }
    }
}

@Composable
private fun ProfileDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = ReadexPro,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = ReadexPro,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddChildProfileDialog(
    onDismissRequest: () -> Unit,
    manageChildProfilesViewModel: ManageChildProfilesViewModel
) {
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(Gender.MALE) }
    var age by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        avatarUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                "Thêm hồ sơ mới",
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = ReadexPro)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AvatarSelectionSection(
                    avatarUri = avatarUri,
                    gender = gender,
                    onSelectImage = { launcher.launch("image/*") }
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên trẻ", fontFamily = ReadexPro) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                GenderSelectionSection(
                    selectedGender = gender,
                    onGenderSelected = { gender = it }
                )

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it.filter { c -> c.isDigit() }.take(2) },
                    label = { Text("Tuổi", fontFamily = ReadexPro) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedAge = age.toIntOrNull() ?: 0
                    val avatar = avatarUri?.toString()
                    manageChildProfilesViewModel.addChildProfile(name, gender, parsedAge, avatar)
                    onDismissRequest()
                },
                enabled = name.isNotBlank() && age.isNotBlank() && age.toIntOrNull()?.let { it > 0 } ?: false,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                )
            ) {
                Text("Xác nhận", fontFamily = ReadexPro)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Hủy", fontFamily = ReadexPro)
            }
        },
        shape = MaterialTheme.shapes.extraLarge
    )
}

@Composable
private fun AvatarSelectionSection(
    avatarUri: Uri?,
    gender: Gender,
    onSelectImage: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onSelectImage)
        ) {
            if (avatarUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(avatarUri),
                    contentDescription = "Ảnh đại diện đã chọn",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(id = getDefaultAvatar(gender)),
                    contentDescription = "Ảnh đại diện mặc định",
                    modifier = Modifier.size(80.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onSelectImage) {
            Text("Chọn ảnh đại diện", fontFamily = ReadexPro)
        }
    }
}

@Composable
private fun GenderSelectionSection(
    selectedGender: Gender,
    onGenderSelected: (Gender) -> Unit
) {
    Column {
        Text(
            text = "Giới tính",
            style = MaterialTheme.typography.labelMedium.copy(fontFamily = ReadexPro),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GenderOption(
                selected = selectedGender == Gender.MALE,
                text = "Nam",
                onSelected = { onGenderSelected(Gender.MALE) }
            )
            GenderOption(
                selected = selectedGender == Gender.FEMALE,
                text = "Nữ",
                onSelected = { onGenderSelected(Gender.FEMALE) }
            )
        }
    }
}

@Composable
private fun GenderOption(
    selected: Boolean,
    text: String,
    onSelected: () -> Unit
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .width(120.dp)
            .clickable(onClick = onSelected)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            RadioButton(
                selected = selected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.outline
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = contentColor, fontFamily = ReadexPro)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManageChildProfilesPreview() {
    HiEmAppTheme {
        ManageChildProfilesScreen()
    }
}