package com.example.ch07.ui.edit

import androidx.lifecycle.ViewModelStore
import com.example.ch07.data.NoteEntity
import com.example.ch07.testutil.FakeNoteRepository
import com.example.ch07.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteEditViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    private val store = ViewModelStore()

    private fun createViewModel(
        repo: FakeNoteRepository,
        noteId: Long? = null,
        clock: () -> Long = { 5_000L }
    ): NoteEditViewModel {
        return NoteEditViewModel(
            repository = repo,
            noteId = noteId,
            clock = clock
        ).also {
            store.put("edit", it)
        }
    }

    @After
    fun tearDown() {
        store.clear()
    }

    @Test
    fun `judul kosong ditolak dan tidak menyimpan apa pun`() = runTest {
        val repo = FakeNoteRepository()
        val vm = createViewModel(repo)

        vm.onTitleChange("   ")
        vm.saveNote()
        runCurrent()

        assertEquals(
            "Judul tidak boleh kosong",
            vm.uiState.value.error
        )
        assertFalse(vm.uiState.value.saved)
        assertTrue(repo.current.isEmpty())
    }

    @Test
    fun `catatan baru memakai clock dan teks dirapikan`() = runTest {
        val repo = FakeNoteRepository()
        val vm = createViewModel(repo, clock = { 5_000L })

        vm.onTitleChange("  Rencana  ")
        vm.onContentChange(" isi ")
        vm.onTagsChange("Kuliah, #Ide, kuliah")
        vm.saveNote()
        runCurrent()

        val saved = repo.current.single()

        assertEquals("Rencana", saved.title)
        assertEquals("isi", saved.content)
        assertEquals(5_000L, saved.createdAt)
        assertEquals(5_000L, saved.updatedAt)
        assertEquals(listOf("kuliah", "ide"), saved.tags)
        assertTrue(vm.uiState.value.saved)
    }

    @Test
    fun `edit memuat data dan mempertahankan identitas catatan`() = runTest {
        val existing = NoteEntity(
            id = 3L,
            title = "Lama",
            content = "isi lama",
            createdAt = 1_000L,
            updatedAt = 2_000L,
            isPinned = true,
            tags = listOf("kuliah", "ide")
        )

        val repo = FakeNoteRepository(listOf(existing))
        val vm = createViewModel(
            repo = repo,
            noteId = 3L,
            clock = { 9_000L }
        )
        runCurrent()

        assertEquals("Lama", vm.uiState.value.title)
        assertEquals("isi lama", vm.uiState.value.content)
        assertEquals("kuliah, ide", vm.uiState.value.tagsText)
        assertFalse(vm.uiState.value.isLoading)

        vm.onTitleChange("  Baru  ")
        vm.onContentChange(" isi baru ")
        vm.onTagsChange("#Tugas, tugas")
        vm.saveNote()
        runCurrent()

        val saved = repo.current.single()

        assertEquals(3L, saved.id)
        assertEquals("Baru", saved.title)
        assertEquals("isi baru", saved.content)
        assertEquals(
            "createdAt tidak boleh tereset",
            1_000L,
            saved.createdAt
        )
        assertTrue("isPinned tidak boleh tereset", saved.isPinned)
        assertEquals(9_000L, saved.updatedAt)
        assertEquals(listOf("tugas"), saved.tags)
        assertTrue(vm.uiState.value.saved)
    }

    @Test
    fun `catatan tidak ditemukan menampilkan pesan`() = runTest {
        val repo = FakeNoteRepository()
        val vm = createViewModel(repo, noteId = 99L)
        runCurrent()

        assertNotNull(vm.uiState.value.error)
        assertFalse(vm.uiState.value.isLoading)
        assertFalse(vm.uiState.value.canSave)

        vm.saveNote()
        runCurrent()

        assertTrue(repo.current.isEmpty())
        assertFalse(vm.uiState.value.saved)
    }
}