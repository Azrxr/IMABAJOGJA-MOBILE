package com.imaba.imabajogja.data.api

import com.imaba.imabajogja.data.response.AdmProfileResponse
import com.imaba.imabajogja.data.response.DocumentsResponse
import com.imaba.imabajogja.data.response.HomeResponse
import com.imaba.imabajogja.data.response.ImportMemberResponse
import com.imaba.imabajogja.data.response.LoginResponse
import com.imaba.imabajogja.data.response.MemberDetailResponse
import com.imaba.imabajogja.data.response.MembersResponse
import com.imaba.imabajogja.data.response.ProfileResponse
import com.imaba.imabajogja.data.response.ProfileUpdateResponse
import com.imaba.imabajogja.data.response.ProgramStudyImportResponse
import com.imaba.imabajogja.data.response.ProgramStudyResponse
import com.imaba.imabajogja.data.response.RegisterAdminResponse
import com.imaba.imabajogja.data.response.RegisterResponse
import com.imaba.imabajogja.data.response.StudyMemberResponse
import com.imaba.imabajogja.data.response.StudyPlansResponse
import com.imaba.imabajogja.data.response.StudyResponse
import com.imaba.imabajogja.data.response.SuccesResponse
import com.imaba.imabajogja.data.response.WilayahResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ApiService {

    //register
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("username") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String
    ): RegisterResponse

    //register admin
    @FormUrlEncoded
    @POST("admin/register")
    suspend fun registerAdmin(
        @Field("username") name: String,
        @Field("fullname") fullname: String,
        @Field("phone_number") phoneNumber: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String
    ): RegisterAdminResponse

    //login
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("login") credential: String,
        @Field("password") password: String
    ): Response<LoginResponse> //LoginResponse

    //home
    @GET("home")
    suspend fun getHomeData(): Response<HomeResponse>

    //editOrganisasiProfile
    @FormUrlEncoded
    @POST("imaba/profileUpdate")
    suspend fun updateOrganizationProfile(
        @Field("title") title: String,
        @Field("description") description: String,
        @Field("vision") vision: String,
        @Field("mission") mission: String,
        @Field("address") address: String,
        @Field("contact_email") contactEmail: String,
        @Field("contact_phone") contactPhone: String,
        @Field("contact_phone2") contactPhone2: String,
    ): Response<SuccesResponse>

    @Multipart
    @POST("imaba/addFile")
    suspend fun addFileOrganization(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<SuccesResponse>

    @DELETE("imaba/deleteFile/{id}")
    suspend fun deleteFileOrganization(
        @Path("id") id: Int
    ): Response<SuccesResponse>

    //List member
    @GET("member/members")
    suspend fun getMembers(
        @Query("search") search: String? = null,
        @Query("generation[]") generation: List<String>? = null,
        @Query("member_type[]") memberType: List<String>? = null,
        @Query("study_plan_status") planStatus: String? = null,
    ): MembersResponse
    //List member Study
    @GET("member/members")
    suspend fun getMembersStudy(
        @Query("search") search: String? = null,
        @Query("study_plan_status") planStatus: String? = null,
    ): MembersResponse

    @GET("member/members")
    suspend fun getMemberSummary(): Response<MembersResponse>

    @DELETE("admin/deleteMember/{id}")
    suspend fun deleteMember(
        @Path("id") id: Int
    ): Response<SuccesResponse>

    @GET("member/profile")
    suspend fun getProfile(
    ): Response<ProfileResponse>

    @GET("admin/profile")
    suspend fun getAdmProfile(
    ): Response<AdmProfileResponse>

    @FormUrlEncoded
    @POST("member/profileUpdate")
    suspend fun updateProfile(
        @Field("username") username: String,
        @Field("email") email: String,

        @Field("fullname") fullname: String,
        @Field("phone_number") phoneNumber: String,

        @Field("province_id") provinceId: Int,
        @Field("regency_id") regencyId: Int,
        @Field("district_id") districtId: Int,
        @Field("full_address") fullAddress: String,
        @Field("kode_pos") kodePos: String,
        @Field("agama") agama: String,
        @Field("nisn") nisn: String,
        @Field("tempat") tempat: String,
        @Field("tanggal_lahir") tanggalLahir: String,
        @Field("gender") gender: String,
        @Field("scholl_origin") schollOrigin: String,
        @Field("tahun_lulus") tahunLulus: Int,
    ): Response<ProfileUpdateResponse>

    @FormUrlEncoded
    @POST("admin/updateMember/{id}")
    suspend fun updateMemberAdm(

        @Path("id") id: Int,
        @Field("fullname") fullname: String,
        @Field("phone_number") phoneNumber: String,
        @Field("province_id") provinceId: Int,
        @Field("regency_id") regencyId: Int,
        @Field("district_id") districtId: Int,
        @Field("full_address") fullAddress: String,
        @Field("kode_pos") kodePos: String,
        @Field("agama") agama: String,
        @Field("nisn") nisn: String,
        @Field("tempat") tempat: String,
        @Field("tanggal_lahir") tanggalLahir: String,
        @Field("gender") gender: String,
        @Field("scholl_origin") schollOrigin: String,
        @Field("tahun_lulus") tahunLulus: Int,

        @Field("angkatan") angkatan: Int,
        @Field("member_type") memberType: String,
    ): Response<SuccesResponse>

    @FormUrlEncoded
    @POST("admin/updateProfile")
    suspend fun updateAdmProfile(
        @Field("username") username: String,
        @Field("email") email: String,

        @Field("fullname") fullname: String,
        @Field("phone_number") phoneNumber: String,

        @Field("provincy_id") provinceId: Int,
        @Field("regency_id") regencyId: Int,
        @Field("district_id") districtId: Int,
        @Field("full_address") fullAddress: String,
    ): Response<SuccesResponse>

    @Multipart
    @POST("member/profileUpdate")
    suspend fun updatePhotoProfile(
        @Part profileImg: MultipartBody.Part
    ): Response<ProfileUpdateResponse>

    @Multipart
    @POST("admin/updateMember/photo/{id}")
    suspend fun updateMemberPhotoProfileAdm(
        @Path ("id") id: Int,
        @Part profileImg: MultipartBody.Part
    ): Response<SuccesResponse>

    @Multipart
    @POST("admin/updateProfile")
    suspend fun updateAdmPhotoProfile(
        @Part profileImg: MultipartBody.Part
    ): Response<SuccesResponse>

    @FormUrlEncoded
    @POST("member/profileUpdate")
    suspend fun updatePassword(
        @Field("current_password") currentPassword: String,
        @Field("new_password") newPassword: String,
        @Field("new_password_confirmation") passwordConfirmation: String,
    ): Response<ProfileUpdateResponse>

    @FormUrlEncoded
    @POST("admin/updateProfile")
    suspend fun updateAdmPassword(
        @Field("current_password") currentPassword: String,
        @Field("new_password") newPassword: String,
        @Field("new_password_confirmation") passwordConfirmation: String,
    ): Response<SuccesResponse>

    @GET("province")
    suspend fun getProvinces(): WilayahResponse

    @GET("regency/{id}")
    suspend fun getRegencies(
        @Path("id") provinceId: Int,
        @Query("search") search: String? = null
    ): WilayahResponse

    // 🔥 GET District List (Kecamatan berdasarkan Regency ID)
    @GET("district/{id}")
    suspend fun getDistricts(
        @Path("id") regencyId: Int,
        @Query("search") search: String? = null
    ): WilayahResponse

    @GET("university")
    suspend fun getUniversity(): StudyResponse

    @GET("faculty/{id}")
    suspend fun getFaculty(
        @Path("id") provinceId: Int,
        @Query("search") search: String? = null
    ): StudyResponse

    @GET("programStudy/{id}")
    suspend fun getProgramStudy(
        @Path("id") provinceId: Int,
        @Query("search") search: String? = null
    ): StudyResponse

    @FormUrlEncoded
    @POST("member/studyPlaneAdd")
    suspend fun addStudyPlan(
        @Field("university_id") universityId: Int,
        @Field("program_study_id") programStudyId: Int
    ): Response<SuccesResponse>

    @GET("member/studyPlane")
    suspend fun getStudyPlane(): StudyPlansResponse

    @FormUrlEncoded
    @POST("member/studyPlaneUpdate/{id}")
    suspend fun updateStudyPlane(
        @Field("id") id: Int,
        @Field("university_id") universityId: Int,
        @Field("program_study_id") programStudyId: Int
    ): Response<SuccesResponse>


    @DELETE("member/studyPlaneDelete/{id}")
    suspend fun deleteStudyPlane(
        @Path("id") id: Int
    ): Response<SuccesResponse>

    @GET("member/showDocument")
    suspend fun getDocuments(): Response<DocumentsResponse>

    @DELETE("member/deleteDocument/{field}")
    suspend fun deleteDocument(
        @Path("field") field: String
    ): Response<SuccesResponse>

    @Multipart
    @POST("member/uploadDocument")
    suspend fun uploadDocument(
        @Part("documentType") documentType: RequestBody,
        @Part file: MultipartBody.Part // File dokumen
    ): Response<SuccesResponse>

    @Multipart
    @POST("member/uploadHomePhoto")
    suspend fun uploadHomePhoto(
        @Part photoImg: MultipartBody.Part,
        @Part("photo_title") photoTitle: RequestBody,
    ): Response<SuccesResponse>

    @DELETE("member/deleteHomePhoto/{id}")
    suspend fun deleteHomePhoto(
        @Path("id") id: Int
    ): Response<SuccesResponse>

    @GET("member/studyMember")
    suspend fun getStudyMember(): Response<StudyMemberResponse>

    @FormUrlEncoded
    @POST("member/updateStudyMember")
    suspend fun updateStudyMember(
        @Field("university_id") universityId: Int,
        @Field("faculty_id") facultyId: Int ?= null,
        @Field("program_study_id") programStudyId: Int
    ): Response<SuccesResponse>

    @FormUrlEncoded
    @POST("admin/study/updateStudyMember/{memberId}")
    suspend fun updateStudyMemberAdm(
        @Path("memberId") memberId: Int,
        @Field("university_id") universityId: Int,
        @Field("faculty_id") facultyId: Int ?= null,
        @Field("program_study_id") programStudyId: Int
    ): Response<SuccesResponse>

    @DELETE("member/deleteStudyMember")
    suspend fun deleteStudyMember(): Response<SuccesResponse>

    @GET("admin/memberDetail/{memberId}")
    suspend fun getMemberDetailAdm(
        @Path("memberId") memberId: Int
    ): Response<MemberDetailResponse>

    @FormUrlEncoded
    @POST("admin/study/{memberId}/PlaneAdd")
    suspend fun addStudyPlanAdm(
        @Path("memberId") memberId: Int,
        @Field("university_id") universityId: Int,
        @Field("program_study_id") programStudyId: Int
    ): Response<SuccesResponse>

    @FormUrlEncoded
    @POST("admin/study/{memberId}/planeUpdate/{studyPlanId}")
    suspend fun updateStudyPlanAdm(
        @Path("memberId") memberId: Int,
        @Path("studyPlanId") studyPlanId: Int,
        @Field("status") status: String,
    ): Response<SuccesResponse>

    @DELETE("admin/document/{memberId}/delete/{field}")
    suspend fun deleteDocumentAdm(
        @Path("memberId") memberId: Int,
        @Path("field") field: String
    ): Response<SuccesResponse>

    @DELETE("admin/document/{Id}/deleteHome")
    suspend fun deleteHomePhotoAdm(
        @Path("Id") id: Int
    ): Response<SuccesResponse>

    @Multipart
    @POST("admin/document/{memberId}/upload/{docId}")
    suspend fun uploadDocumentAdm(
        @Path("memberId") memberId: Int,
        @Path("docId") docId: Int,
        @Part("documentType") documentType: RequestBody,
        @Part file: MultipartBody.Part // File dokumen
    ): Response<SuccesResponse>

    @Multipart
    @POST("admin/document/{memberId}/uploadHome")
    suspend fun uploadHomePhotoAdm(
        @Path("memberId") memberId: Int,
        @Part photoImg: MultipartBody.Part,
        @Part("photo_title") photoTitle: RequestBody,
    ): Response<SuccesResponse>

    @Multipart
    @POST("admin/member/import-excel")
    suspend fun importMember(
        @Part file: MultipartBody.Part
    ): Response<ImportMemberResponse>

    @Multipart
    @POST("admin/study/import")
    suspend fun importProgramStudy(
        @Part file: MultipartBody.Part
    ): Response<ProgramStudyImportResponse>

    @GET("admin/member/export-excel")
    @Streaming
    suspend fun exportMembers(
        @Query("angkatan[]") generations: List<String>?,
        @Query("member_type[]") memberTypes: List<String>?
    ): Response<ResponseBody>

    @GET("study/getAll")
    suspend fun getAllProgramStudy(
        @Query("search") search: String? = null,
        @Query("jenjang") jenjang: String? = null,
    ): Response<ProgramStudyResponse>

}